package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.vertx.MutinyHelper;
import io.vertx.core.Vertx;
import org.stellar.sdk.*;
import org.stellar.sdk.operations.CreateAccountOperation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StellarChannelGeneratorOperation {
    private Server server;
    private Network network;

    public StellarChannelGeneratorOperation(Server server, Network network) {
        this.server = server;
        this.network = network;
    }

    public Uni<List<StellarChannelGenerator>> create(StellarChannelGeneratorOperationPayload payload) {
        return Uni.createFrom().item(() -> {
                    int numOfAccountsPerChannelGenerator = payload.maxVoters() / payload.numOfGeneratorsToCreate();
                    int numOfAccountPerChannelGeneratorRemainder = payload.maxVoters() % payload.numOfGeneratorsToCreate();

                    List<StellarChannelGenerator> generators = new ArrayList<>();

                    KeyPair fundingKeyPair = KeyPair.fromSecretSeed(payload.fundingAccountSecret());

                    var fundingAccount = server.loadAccount(fundingKeyPair.getAccountId());
                    var transactionBuilder = new TransactionBuilder(fundingAccount, network);

                    for(int i = 0; i < payload.numOfGeneratorsToCreate() - 1; i++) {
                        var channelGenKeyPair = prepareAccountCreationOn(transactionBuilder, numOfAccountsPerChannelGenerator);
                        var channelGenSecret = new String(channelGenKeyPair.getSecretSeed());
                        generators.add(
                                new StellarChannelGenerator(channelGenSecret, numOfAccountsPerChannelGenerator, payload.votingId())
                        );
                    }

                    var lastChannelGenNumOfAccounts = numOfAccountsPerChannelGenerator + numOfAccountPerChannelGeneratorRemainder;
                    var channelGenKeyPair = prepareAccountCreationOn(transactionBuilder, lastChannelGenNumOfAccounts);
                    var channelGenSecret = new String(channelGenKeyPair.getSecretSeed());
                    generators.add(
                            new StellarChannelGenerator(channelGenSecret, lastChannelGenNumOfAccounts, payload.votingId())
                    );

                    var transaction = transactionBuilder.build();
                    transaction.sign(fundingKeyPair);

                    server.submitTransaction(transaction);

                    return generators;
                })
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .emitOn(MutinyHelper.executor(Vertx.currentContext()));
    }

    private KeyPair prepareAccountCreationOn(TransactionBuilder txBuilder, int numOfAccountsPerChannel) {
        KeyPair keyPair = KeyPair.random();
        BigDecimal startingBalance = new BigDecimal(numOfAccountsPerChannel * 2 + 10);

        var createAccountOperation = CreateAccountOperation.builder()
                .destination(keyPair.getAccountId())
                .startingBalance(startingBalance)
                .build();
        txBuilder.addOperation(createAccountOperation);

        return keyPair;
    }
}
