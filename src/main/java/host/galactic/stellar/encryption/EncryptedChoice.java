package host.galactic.stellar.encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;

public class EncryptedChoice {
    private static final int RANDOM_IV_LENGTH = 8;
    private static final int KEY_LENGTH_BYTES = 256;

    private static final SecureRandom secureRandom = new SecureRandom();

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String generateKeyBase64() {
        try {
            var keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(KEY_LENGTH_BYTES);
            var keyBytes = keyGenerator.generateKey().getEncoded();
            return Base64.getEncoder().encodeToString(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encryptToBase64(String message, String keyBase64) {
        var messageBytes = message.getBytes();
        var keyBytes = Base64.getDecoder().decode(keyBase64);
        var secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        try {
            var aes = Cipher.getInstance("AES/CTR/NoPadding", BouncyCastleProvider.PROVIDER_NAME);
            var randomIvBytes = randomIv();
            var ivParameterSpec = new IvParameterSpec(randomIvBytes);

            aes.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            var encryptedBytes = aes.doFinal(messageBytes);
            var resultBytes = new byte[randomIvBytes.length + encryptedBytes.length];

            System.arraycopy(randomIvBytes, 0, resultBytes, 0, randomIvBytes.length);
            System.arraycopy(encryptedBytes, 0, resultBytes, randomIvBytes.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(resultBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptFromBase64(String cipherBase64, String keyBase64) {
        var cipherBytes = Base64.getDecoder().decode(cipherBase64);
        var keyBytes = Base64.getDecoder().decode(keyBase64);
        var secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        try {
            var aes = Cipher.getInstance("AES/CTR/NoPadding", BouncyCastleProvider.PROVIDER_NAME);
            var randomIvBytes = Arrays.copyOfRange(cipherBytes, 0, RANDOM_IV_LENGTH);
            var ivParameterSpec = new IvParameterSpec(randomIvBytes);

            aes.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            var encryptedBytes = Arrays.copyOfRange(cipherBytes, RANDOM_IV_LENGTH, cipherBytes.length);
            var decryptedBytes = aes.doFinal(encryptedBytes);

            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] randomIv() {
        var randomBytes = new byte[RANDOM_IV_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }

    private EncryptedChoice() {
    }
}
