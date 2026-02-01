package host.galactic.stellar.operations;

import org.stellar.sdk.KeyPair;

import java.math.BigDecimal;

public class StellarUtils {
    public static String toTruncatedAccountId(String accountSecret) {
        return toAccountId(accountSecret).substring(0, 5) + "...";
    }

    public static String toTruncatedAccountId(KeyPair keyPair) {
        return toTruncatedAccountId(new String(keyPair.getSecretSeed()));
    }

    public static String toAccountId(String accountSecret) {
        return KeyPair.fromSecretSeed(accountSecret).getAccountId();
    }

    public static BigDecimal toAssetAmount(long value) {
        BigDecimal valueAsBigDecimal = new BigDecimal(value);
        BigDecimal divisor = new BigDecimal(10).pow(7);
        return valueAsBigDecimal.divide(divisor);
    }
}
