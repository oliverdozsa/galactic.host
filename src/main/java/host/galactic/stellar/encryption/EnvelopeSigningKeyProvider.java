package host.galactic.stellar.encryption;

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
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.eclipse.microprofile.config.ConfigProvider;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.SecureRandom;

public class EnvelopeSigningKeyProvider {
    private AsymmetricCipherKeyPair keyPair;
    private String publicKeyAsPem;

    private static AsymmetricCipherKeyPair testKeyPair = generateKeyPair();

    @PostConstruct
    private void init() {
        keyPair = readKeyPair();
        publicKeyAsPem = publicKeyAsPem();
    }

    @Produces
    @Named("signing")
    public AsymmetricCipherKeyPair provideKeyPair() {
        return keyPair;
    }

    @Produces
    @Named("signingPublicKeyPem")
    public String providePublicKeyAsPem() {
        return publicKeyAsPem;
    }

    private String publicKeyAsPem() {
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
        if (LaunchMode.current() != LaunchMode.NORMAL) {
            Log.info("Using random signing keypair for dev and test modes.");
            return privateToPemString(testKeyPair);
        } else {
            return ConfigProvider.getConfig().getValue("galactic.host.voting.signing.key", String.class);
        }
    }

    private static AsymmetricCipherKeyPair generateKeyPair() {
        RSAKeyPairGenerator generator = new RSAKeyPairGenerator();

        BigInteger publicExponent = new BigInteger("10001", 16);
        SecureRandom random = new SecureRandom();
        RSAKeyGenerationParameters keyGenParams = new RSAKeyGenerationParameters(
                publicExponent, random, 2048, 80
        );

        generator.init(keyGenParams);
        return generator.generateKeyPair();
    }

    private static String privateToPemString(AsymmetricCipherKeyPair keyPair) {
        try (var sw = new StringWriter(); var pw = new JcaPEMWriter(sw)) {
            var converter = new JcaPEMKeyConverter();
            converter.setProvider("BC");

            var pkInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(keyPair.getPrivate());

            pw.writeObject(converter.getPrivateKey(pkInfo));
            pw.flush();
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
