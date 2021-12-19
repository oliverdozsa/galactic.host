package extractors;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Result;

import static extractors.GenericDataFromResult.jsonOf;

public class CommissionResponseFromResult {
    public static String publicKeyOf(Result result) {
        JsonNode initResponse = jsonOf(result);
        return initResponse.get("publicKey").asText();
    }

    public static String sessionJwtOf(Result result) {
        JsonNode initResponse = jsonOf(result);
        return initResponse.get("sessionJwt").asText();
    }

    public static String envelopeSignatureOf(Result result) {
        // TODO
        return null;
    }
}
