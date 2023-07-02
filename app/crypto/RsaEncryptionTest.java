package crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.util.Base64;

public class RsaEncryptionTest {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) throws Exception {
        String secretMessage = "{\"@context\":\"https://www.w3.org/ns/activitystreams\",\"type\":\"Create\",\"id\":\"https://example.net/~mallory/87374\",\"actor\":\"https://example.net/~mallory\",\"object\":{\"id\":\"https://example.com/~mallory/note/72\",\"type\":\"Note\",\"attributedTo\":\"https://example.net/~mallory\",\"content\":\"This is a note\",\"published\":\"2015-02-10T15:04:55Z\",\"to\":[\"https://example.org/~john/\"],\"cc\":[\"https://example.com/~erik/followers\",\"https://www.w3.org/ns/activitystreams#Public\"]},\"published\":\"2015-02-10T15:04:55Z\",\"to\":[\"https://example.org/~john/\"],\"cc\":[\"https://example.com/~erik/followers\",\"https://www.w3.org/ns/activitystreams#Public\"]}";
        byte[] secretMessageBytes = secretMessage.getBytes();
        byte[] key = AesCtrCrypto.generateKey();

        byte[] encryptedMessageBytes = AesCtrCrypto.encrypt(key, secretMessageBytes);
        String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);

        // Signing with RSA
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(4096, new SecureRandom());
        KeyPair pair = generator.generateKeyPair();
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(pair.getPrivate());
        privateSignature.update(secretMessageBytes);

        byte[] signature = privateSignature.sign();

        System.out.println("encrypted message (base64) = " + encodedMessage);
        System.out.println("message signature (base64) = " + Base64.getEncoder().encodeToString(signature));
        System.out.println("decrypted message = " + new String(AesCtrCrypto.decrypt(key, encryptedMessageBytes)));

        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(pair.getPublic());
        publicSignature.update(secretMessageBytes);
        boolean isSignatureValid = publicSignature.verify(signature);
        System.out.println("signature is valid: "+ isSignatureValid);
    }
}
