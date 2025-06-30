package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import org.stellar.sdk.*;
import org.stellar.sdk.operations.PaymentOperation;

import java.math.BigDecimal;

import static org.stellar.sdk.AbstractTransaction.MIN_BASE_FEE;

class StellarOperationsImp implements StellarOperations {
    private static final Server testServer = new Server("https://horizon-testnet.stellar.org");
    private static final Server mainServer = new Server("https://horizon.stellar.org");

    private final Server server;
    private final Network network;

    public StellarOperationsImp(boolean isOnTestNet) {
        server = isOnTestNet ? testServer : mainServer;
        network = isOnTestNet ? Network.TESTNET : Network.PUBLIC;
    }

    public Uni<Void> transferXlmFrom(String sourceAccountSecret, double xlm, String targetAccountSecret) {
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
        }).runSubscriptionOn(Infrastructure.getDefaultExecutor());
    }

    private static String toTruncatedAccountId(String accountSecret) {
        return toAccountId(accountSecret).substring(0, 10) + "...";
    }

    private static String toAccountId(String accountSecret) {
        return KeyPair.fromSecretSeed(accountSecret).getAccountId();
    }
}
