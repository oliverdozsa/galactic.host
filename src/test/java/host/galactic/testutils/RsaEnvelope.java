package host.galactic.testutils;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.engines.RSABlindingEngine;
import org.bouncycastle.crypto.generators.RSABlindingFactorGenerator;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.openssl.PEMParser;

import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RsaEnvelope {
    private final RSABlindingFactorGenerator blindingFactorGenerator;
    private final RSABlindingParameters blindingParameters;

    public RsaEnvelope(String publicKeyPem) {
        this(toRsaPublicKey(publicKeyPem));
    }

    public RsaEnvelope(RSAKeyParameters publicKey) {
        blindingFactorGenerator = new RSABlindingFactorGenerator();
        blindingFactorGenerator.init(publicKey);
        var blindingFactor = blindingFactorGenerator.generateBlindingFactor();
        blindingParameters = new RSABlindingParameters(publicKey, blindingFactor);
    }

    public byte[] create(byte[] content) {
        try {
            var contentDigest = MessageDigest.getInstance("SHA-384").digest(content);

            var engine = new RSABlindingEngine();
            engine.init(true, blindingParameters);
            return engine.processBlock(contentDigest, 0, contentDigest.length);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] revealedSignature(byte[] signatureOnEnvelope) {
        RSABlindingEngine engine = new RSABlindingEngine();
        engine.init(false, blindingParameters);
        return engine.processBlock(signatureOnEnvelope, 0, signatureOnEnvelope.length);
    }

    public static RSAKeyParameters toRsaPublicKey(String pem) {
        var pemStringReader = new StringReader(pem);
        var pemParser = new PEMParser(pemStringReader);

        try {
            var pkInfo = (SubjectPublicKeyInfo) pemParser.readObject();
            return (RSAKeyParameters) PublicKeyFactory.createKey(pkInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
