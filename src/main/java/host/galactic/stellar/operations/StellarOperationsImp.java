package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;
import io.quarkus.logging.Log;
import org.stellar.sdk.*;
import org.stellar.sdk.operations.PaymentOperation;

import java.math.BigDecimal;

import static org.stellar.sdk.AbstractTransaction.MIN_BASE_FEE;

class StellarOperationsImp implements StellarOperations {
    private final Server server;
    private final Network network;

    public StellarOperationsImp(boolean isOnTestNet) {
        String serverUri = isOnTestNet ? "https://horizon-testnet.stellar.org" : "https://horizon.stellar.org";
        server = new Server(serverUri);
        network = isOnTestNet ? Network.TESTNET : Network.PUBLIC;
    }

    public Uni<Void> transferXlmFrom(String sourceAccountSecret, double xlm, String targetAccountSecret) {
        String sourceAccountId = toTruncatedAccountId(sourceAccountSecret);
        String targetAccountId = toTruncatedAccountId(targetAccountSecret);

        Log.infof("transferXlmFrom(): Transferring: %s -> %s XLMs -> %s", sourceAccountId, xlm, targetAccountId);

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

        // TODO: Wrap operation in a Uni
        return Uni.createFrom().voidItem();
    }

    public void done() {
        server.close();
    }

    private static String toTruncatedAccountId(String accountSecret) {
        return toAccountId(accountSecret).substring(0, 10) + "...";
    }

    private static String toAccountId(String accountSecret) {
        return KeyPair.fromSecretSeed(accountSecret).getAccountId();
    }
}
