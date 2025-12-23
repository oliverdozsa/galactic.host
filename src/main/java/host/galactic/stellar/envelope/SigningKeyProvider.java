package host.galactic.stellar.envelope;

import io.quarkus.logging.Log;
import io.quarkus.runtime.LaunchMode;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
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
    private String publicKesAsPem;

    @PostConstruct
    private void init() {
        if (keyPair == null) {
            keyPair = readKeyPair();
        }
    }

    @Produces
    @Named("signing")
    public AsymmetricCipherKeyPair provideKeyPair() {
        return keyPair;
    }

    @Produces
    @Named("signingPublicKeyPem")
    public String providePublicKeyAsPem() {
        try (var sw = new StringWriter(); var pw = new JcaPEMWriter(sw)) {
            var publicKeyInfo = SubjectPublicKeyInfoFactory
                    .createSubjectPublicKeyInfo((RSAKeyParameters) keyPair.getPublic());
            pw.writeObject(publicKeyInfo);
            pw.flush();
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AsymmetricCipherKeyPair readKeyPair() {
        var keyPairAsPem = getKeyPairPem();
        if (keyPairAsPem == null || keyPairAsPem.isBlank()) {
            Log.error("galactic.host.voting.signing.key cannot be empty!");
            throw new RuntimeException("galactic.host.voting.signing.key cannot be empty!");
        }

        Log.infof("keyPairAsPem = %s", keyPairAsPem);

        var stringReader = new StringReader(keyPairAsPem);
        var pemParser = new PEMParser(stringReader);
        try {
            var pemKeyPair = (PEMKeyPair) pemParser.readObject();
            var privateParam = PrivateKeyFactory.createKey(pemKeyPair.getPrivateKeyInfo());
            var publicParam = PublicKeyFactory.createKey(pemKeyPair.getPublicKeyInfo());

            return new AsymmetricCipherKeyPair(publicParam, privateParam);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read signing key!", e);
        }
    }

    private String getKeyPairPem() {
        return ConfigProvider.getConfig().getValue("galactic.host.voting.signing.key", String.class);
    }
}
