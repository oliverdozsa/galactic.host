package responses.social;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActorResponse {
    private static final String context = "https://www.w3.org/ns/activitystreams";

    private final String baseUrl;

    public ActorResponse(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @JsonProperty("@context")
    public String getContext() {
        return context;
    }
}
