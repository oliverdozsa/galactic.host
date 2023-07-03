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

    public static String followingOf(Result result) {
        JsonNode jsonNode = jsonOf(result);
        return jsonNode.get("following").asText();
    }

    public static String followersOf(Result result) {
        JsonNode jsonNode = jsonOf(result);
        return jsonNode.get("followers").asText();
    }

    public static String likedOf(Result result) {
        JsonNode jsonNode = jsonOf(result);
        return jsonNode.get("liked").asText();
    }

    public static String inboxOf(Result result) {
        JsonNode jsonNode = jsonOf(result);
        return jsonNode.get("inbox").asText();
    }

    public static String outboxOf(Result result) {
        JsonNode jsonNode = jsonOf(result);
        return jsonNode.get("outbox").asText();
    }

    public static String typeOf(Result result) {
        JsonNode jsonNode = jsonOf(result);
        return jsonNode.get("type").asText();
    }

    public static String nameOf(Result result) {
        JsonNode jsonNode = jsonOf(result);
        return jsonNode.get("name").asText();
    }

    public static String preferredUsernameOf(Result result) {
        JsonNode jsonNode = jsonOf(result);
        return jsonNode.get("preferredUsername").asText();
    }
}
