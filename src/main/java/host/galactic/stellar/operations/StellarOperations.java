package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;
import io.quarkus.logging.Log;

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
        Log.infof("transferXlmFrom(): Transferring %s XLMs", xlm);
        // TODO
        return Uni.createFrom().voidItem();
    }

    private static String toTruncatedAccountPublic(String accountSecret) {
        return toAccountPublic(accountSecret).substring(0, 10) + "...";
    }

    private static String toAccountPublic(String accountSecret) {
        return "";
    }
}
