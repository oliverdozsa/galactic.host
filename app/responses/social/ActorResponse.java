package responses.social;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActorResponse {
    private static final String context = "https://www.w3.org/ns/activitystreams";

    private static final String type = "Person";

    private String id;

    private String following;

    private String followers;

    private String liked;

    private String inbox;

    private String outbox;

    private String preferredUsername;

    public ActorResponse() {
    }

    @JsonProperty("@context")
    public String getContext() {
        return context;
    }

    public String getType() {
        return type;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getLiked() {
        return liked;
    }

    public void setLiked(String liked) {
        this.liked = liked;
    }

    public String getInbox() {
        return inbox;
    }

    public void setInbox(String inbox) {
        this.inbox = inbox;
    }

    public String getOutbox() {
        return outbox;
    }

    public void setOutbox(String outbox) {
        this.outbox = outbox;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    public void setPreferredUsername(String preferredUsername) {
        this.preferredUsername = preferredUsername;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
