package components.extractors.social;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

import static components.extractors.GenericDataFromResult.jsonOf;

public class ActorResponseFromResult {
    public static String contextOf(Result result) {
        JsonNode jsonNode = jsonOf(result);
        return jsonNode.get("@context").asText();
    }

    public static String idOf(Result result) {
        JsonNode jsonNode = jsonOf(result);
        return jsonNode.get("id").asText();
    }
}
