package host.galactic.stellar.operations;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.vertx.MutinyHelper;
import io.vertx.core.Vertx;
import org.stellar.sdk.*;
import org.stellar.sdk.operations.CreateAccountOperation;

import java.math.BigDecimal;

import static host.galactic.stellar.operations.StellarUtils.toAccountId;
import static host.galactic.stellar.operations.StellarUtils.toTruncatedAccountId;
import static org.stellar.sdk.AbstractTransaction.MIN_BASE_FEE;

class StellarCreateInternalFunding {
    private Server server;
    private Network network;

    StellarCreateInternalFunding(Server server, Network network) {
        this.server = server;
        this.network = network;
    }

    public Uni<Void> create(String sourceAccountSecret, double startingXlm, String targetAccountSecret) {
        return Uni.createFrom().<Void>item(() -> {
                    String truncatedTargetAccountId = toTruncatedAccountId(targetAccountSecret);
                    Log.infof("[STELLAR]: Creating internal funding %s with starting balance: %d XLMs", truncatedTargetAccountId, startingXlm);

                    String sourceAccountId = toAccountId(sourceAccountSecret);
                    String targetAccountId = toAccountId(targetAccountSecret);


                    TransactionBuilderAccount sourceAccount = server.loadAccount(sourceAccountId);

                    var createAccountOperation = CreateAccountOperation.builder()
                            .destination(targetAccountId)
                            .startingBalance(new BigDecimal(startingXlm))
                            .build();

                    Transaction transaction = new TransactionBuilder(sourceAccount, network)
                            .setBaseFee(MIN_BASE_FEE)
                            .setTimeout(30)
                            .addOperation(createAccountOperation)
                            .build();

                    KeyPair sourceKeyPair = KeyPair.fromSecretSeed(sourceAccountSecret);
                    transaction.sign(sourceKeyPair);

                    StellarSubmitTransaction.submit("internal funding", transaction, server);

                    Log.infof("[STELLAR]: Successfully created internal funding account.");
                    return null;
                })
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .emitOn(MutinyHelper.executor(Vertx.currentContext()));
    }
}
