package host.galactic.stellar.operations;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.vertx.MutinyHelper;
import io.vertx.core.Vertx;
import org.stellar.sdk.*;
import org.stellar.sdk.operations.CreateAccountOperation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.stellar.sdk.AbstractTransaction.MIN_BASE_FEE;

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

                    Log.infof("[STELLAR]: Total channel accounts to create: %d for voting %d", payload.maxVoters(), payload.votingId());
                    Log.infof("[STELLAR]: Creating %d channel generators for voting %d", payload.numOfGeneratorsToCreate(), payload.votingId());
                    Log.infof("[STELLAR]: The number of accounts per channel generators to create is %d. The last generator will additionally create %d channel accounts.",
                            numOfAccountsPerChannelGenerator, numOfAccountPerChannelGeneratorRemainder);

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

                    var transaction = transactionBuilder
                            .setBaseFee(MIN_BASE_FEE)
                            .setTimeout(15)
                            .build();

                    transaction.sign(fundingKeyPair);
                    StellarSubmitTransaction.submit("create channel generators", transaction, server);

                    Log.info("[STELLAR]: Successfully created channel generators.");

                    return generators;
                })
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .emitOn(MutinyHelper.executor(Vertx.currentContext()));
    }

    private KeyPair prepareAccountCreationOn(TransactionBuilder txBuilder, int numOfAccountsPerChannel) {
        var keyPair = KeyPair.random();
        var startingBalance = new BigDecimal(numOfAccountsPerChannel * 2 + 10);

        var createAccountOperation = CreateAccountOperation.builder()
                .destination(keyPair.getAccountId())
                .startingBalance(startingBalance)
                .build();
        txBuilder.addOperation(createAccountOperation);

        return keyPair;
    }
}
