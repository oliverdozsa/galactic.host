package host.galactic.stellar;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class StellarBaseTestUtils {
    public String toBase64(String message) {
        return toBase64(message.getBytes(StandardCharsets.UTF_8));
    }

    public String toBase64(byte[] message) {
        var base64Bytes = Base64.getEncoder().encode(message);
        return new String(base64Bytes);
    }

    public byte[] fromBase64(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }
}
