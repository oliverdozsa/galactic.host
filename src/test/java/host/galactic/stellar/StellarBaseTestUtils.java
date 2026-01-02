package host.galactic.stellar;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class StellarBaseTestUtils {
    public String createEnvelopeFor(String message) {
        var base64Bytes = Base64.getEncoder().encode(message.getBytes(StandardCharsets.UTF_8));
        return new String(base64Bytes);
    }
}
