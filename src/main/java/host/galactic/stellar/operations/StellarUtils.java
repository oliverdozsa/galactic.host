package host.galactic.stellar.operations;

import org.stellar.sdk.KeyPair;

public class StellarUtils {
    public static String toTruncatedAccountId(String accountSecret) {
        return toAccountId(accountSecret).substring(0, 5) + "...";
    }

    public static String toAccountId(String accountSecret) {
        return KeyPair.fromSecretSeed(accountSecret).getAccountId();
    }
}
