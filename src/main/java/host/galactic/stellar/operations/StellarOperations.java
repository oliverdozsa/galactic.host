package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;
import io.quarkus.logging.Log;
import org.stellar.sdk.KeyPair;

public class StellarOperations {
    private final String url;

    public StellarOperations(boolean isOnTestNet) {
        if(isOnTestNet) {
            url = "https://horizon-testnet.stellar.org";
        } else {
            url = "https://horizon.stellar.org";
        }
    }

    public Uni<Void> transferXlmFrom(String sourceAccountSecret, double xlm, String targetAccountSecret) {
        String sourceAccountId = toTruncatedAccountId(sourceAccountSecret);
        String targetAccountId = toTruncatedAccountId(targetAccountSecret);

        Log.infof("transferXlmFrom(): Transferring: %s -> %s XLMs -> %s", sourceAccountId, xlm, targetAccountId);
        // TODO
        return Uni.createFrom().voidItem();
    }

    private static String toTruncatedAccountId(String accountSecret) {
        return toAccountId(accountSecret).substring(0, 10) + "...";
    }

    private static String toAccountId(String accountSecret) {
        return KeyPair.fromSecretSeed(accountSecret).getAccountId();
    }
}
