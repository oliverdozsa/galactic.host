package host.galactic.stellar.operations;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import org.stellar.sdk.*;
import org.stellar.sdk.operations.CreateAccountOperation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StellarChannelAccountOperation {
    private Server server;
    private Network network;

    private static final BigDecimal STARTING_BALANCE_XLM = new BigDecimal(2);

    public StellarChannelAccountOperation(Server server, Network network) {
        this.server = server;
        this.network = network;
    }

    public Uni<List<StellarChannelAccount>> create(StellarChannelAccountOperationPayload payload) {
        return Uni.createFrom().item(() -> {
            Log.infof("[STELLAR]: Creating %d channel accounts for voting %d", payload.numOfAccountsToCreate(), payload.votingId());
            var generatorKeyPair = KeyPair.fromSecretSeed(payload.generatorAccountSecret());
            var generatorAccount = server.loadAccount(generatorKeyPair.getAccountId());
            var transactionBuilder = new TransactionBuilder(generatorAccount, network);

            var channelAccounts = new ArrayList<StellarChannelAccount>();

            for (var i = 0; i < payload.numOfAccountsToCreate(); i++) {
                var channelKeyPair = prepareAccountCreationOn(transactionBuilder);
                channelAccounts.add(from(channelKeyPair, payload.votingId()));
            }

            return channelAccounts;
        });
    }

    private KeyPair prepareAccountCreationOn(TransactionBuilder txBuilder) {
        var channelKeyPair = KeyPair.random();

        var createAccountOperation = CreateAccountOperation.builder()
                .destination(channelKeyPair.getAccountId())
                .startingBalance(STARTING_BALANCE_XLM)
                .build();

        txBuilder.addOperation(createAccountOperation);

        return channelKeyPair;
    }

    private StellarChannelAccount from(KeyPair keyPair, Long votingId) {
        return new StellarChannelAccount(new String(keyPair.getSecretSeed()), votingId);
    }
}
