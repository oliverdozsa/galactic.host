package host.galactic.stellar.envelope;

import io.quarkus.logging.Log;
import io.quarkus.runtime.LaunchMode;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.eclipse.microprofile.config.ConfigProvider;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.SecureRandom;

public class SigningKeyProvider {
    private AsymmetricCipherKeyPair keyPair;


    @Produces
    @Named("signing")
    AsymmetricCipherKeyPair provide() {
        if (keyPair == null) {
            readKeyPair();
        }

        return keyPair;
    }

    private void readKeyPair() {
        var keyPairAsPem = getKeyPairPem();
        if (keyPairAsPem == null || keyPairAsPem.isBlank()) {
            Log.error("galactic.host.voting.signing.key cannot be empty!");
            throw new RuntimeException("galactic.host.voting.signing.key cannot be empty!");
        }

        var stringReader = new StringReader(keyPairAsPem);
        var pemParser = new PEMParser(stringReader);
        try {
            var pemKeyPair = (PEMKeyPair) pemParser.readObject();
            var privateParam = PrivateKeyFactory.createKey(pemKeyPair.getPrivateKeyInfo());
            var publicParam = PublicKeyFactory.createKey(pemKeyPair.getPublicKeyInfo());

            keyPair = new AsymmetricCipherKeyPair(publicParam, privateParam);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read signing key!", e);
        }
    }

    private String getKeyPairPem() {
        if (LaunchMode.current() != LaunchMode.NORMAL) {
            Log.info("Generating random signing keypair for dev and test modes.");
            var keyPair = generateKeyPair();
            return privateToPemString(keyPair);
        } else {
            return ConfigProvider.getConfig().getValue("galactic.host.voting.signing.key", String.class);
        }
    }

    private AsymmetricCipherKeyPair generateKeyPair() {
        RSAKeyPairGenerator generator = new RSAKeyPairGenerator();

        BigInteger publicExponent = new BigInteger("10001", 16);
        SecureRandom random = new SecureRandom();
        RSAKeyGenerationParameters keyGenParams = new RSAKeyGenerationParameters(
                publicExponent, random, 4096, 80
        );

        generator.init(keyGenParams);
        return generator.generateKeyPair();
    }

    private String privateToPemString(AsymmetricCipherKeyPair keyPair) {
        var pemStringWriter = new StringWriter();

        try {
            var privateKeyInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(keyPair.getPrivate());

            var pemObject = new PemObject("RSA PRIVATE KEY", privateKeyInfo.getEncoded());
            var pemWriter = new PemWriter(pemStringWriter);
            pemWriter.writeObject(pemObject);
            pemWriter.close();

            return pemStringWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
