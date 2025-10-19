package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;
import org.stellar.sdk.*;

import java.util.ArrayList;
import java.util.List;

public class StellarChannelAccountOperation {
    private Server server;
    private Network network;

    public StellarChannelAccountOperation(Server server, Network network) {
        this.server = server;
        this.network = network;
    }

    public Uni<List<StellarChannelAccount>> create(StellarChannelAccountOperationPayload payload) {
        return Uni.createFrom().item(() -> {
            var generatorKeyPair = KeyPair.fromSecretSeed(payload.generatorAccountSecret());
            var generatorAccount = server.loadAccount(generatorKeyPair.getAccountId());
            var transactionBuilder = new TransactionBuilder(generatorAccount, network);

            var channelAccounts = new ArrayList<StellarChannelAccount>();

            for(var i = 0; i < payload.numOfAccountsToCreate(); i++) {
                var channelKeyPair = prepareAccountCreationOn(transactionBuilder);
                channelAccounts.add(from(channelKeyPair, payload.votingId()));
            }

            // TODO

            return null;
        });
    }

    private KeyPair prepareAccountCreationOn(TransactionBuilder txBuilder) {
        var channelKeyPair = KeyPair.random();

        // TODO

        return channelKeyPair;
    }

    private StellarChannelAccount from(KeyPair keyPair, Long votingId) {
        return new StellarChannelAccount(new String(keyPair.getSecretSeed()), votingId);
    }
}
