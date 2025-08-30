package host.galactic.stellar.operations;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.vertx.MutinyHelper;
import io.vertx.core.Vertx;
import org.stellar.sdk.*;
import org.stellar.sdk.operations.PaymentOperation;

import java.math.BigDecimal;

import static host.galactic.stellar.operations.StellarUtils.toAccountId;
import static host.galactic.stellar.operations.StellarUtils.toTruncatedAccountId;
import static org.stellar.sdk.AbstractTransaction.MIN_BASE_FEE;

class StellarTransferXlmOperation {
    private Server server;
    private Network network;

    StellarTransferXlmOperation(Server server, Network network) {
        this.server = server;
        this.network = network;
    }

    public Uni<Void> transfer(String sourceAccountSecret, double xlm, String targetAccountSecret) {
        return Uni.createFrom().<Void>item(() -> {
                    String truncatedSourceAccountId = toTruncatedAccountId(sourceAccountSecret);
                    String truncatedTargetAccountId = toTruncatedAccountId(targetAccountSecret);
                    Log.infof("transferXlmFrom(): Transferring: %s -> %s XLMs -> %s", truncatedSourceAccountId, xlm, truncatedTargetAccountId);

                    String sourceAccountId = toAccountId(sourceAccountSecret);
                    String targetAccountId = toAccountId(targetAccountSecret);

                    TransactionBuilderAccount sourceAccount = server.loadAccount(sourceAccountId);
                    PaymentOperation paymentOperation = PaymentOperation.builder()
                            .destination(targetAccountId)
                            .asset(Asset.createNativeAsset())
                            .amount(new BigDecimal(xlm))
                            .build();

                    Transaction transaction = new TransactionBuilder(sourceAccount, network)
                            .setBaseFee(MIN_BASE_FEE)
                            .setTimeout(30)
                            .addOperation(paymentOperation)
                            .build();

                    KeyPair sourceKeyPair = KeyPair.fromSecretSeed(sourceAccountSecret);
                    transaction.sign(sourceKeyPair);

                    server.submitTransaction(transaction);
                    Log.infof("transferXlmFrom(): Transfer successful: %s -> %s XLMs -> %s", truncatedSourceAccountId, xlm, truncatedTargetAccountId);
                    return null;
                })
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .emitOn(MutinyHelper.executor(Vertx.currentContext()));
    }
}
