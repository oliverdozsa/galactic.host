package components.controllers.social;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.social.routes;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Result;
import requests.social.SignupRequest;

import static components.extractors.GenericDataFromResult.jsonOf;
import static components.extractors.GenericDataFromResult.statusOf;
import static matchers.ResultHasHeader.hasLocationHeader;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static play.mvc.Http.HeaderNames.LOCATION;
import static play.mvc.Http.Status.CREATED;
import static play.mvc.Http.Status.OK;

public class TestOutbox extends SocialTest {
    @Before
    public void setup() {
        super.setup();
        signupAlice();
    }

    @Test
    public void testCreateActivity() {
        // Given
        String actorId = routes.SocialController.getActor("alice").url();

        String createNoteJson = "{\n" +
                "  \"@context\": \"https://www.w3.org/ns/activitystreams\",\n" +
                "  \"type\": \"Create\",\n" +
                "  \"actor\": \"" + actorId + "\",\n" +
                "  \"object\": {\n" +
                "    \"type\": \"Note\",\n" +
                "    \"content\": \"This is a note\",\n" +
                "    \"published\": \"2015-02-10T15:04:55Z\",\n" +
                "    \"to\": [\"https://example.org/~john/\"],\n" +
                "    \"cc\": [\"https://example.com/~erik/followers\",\n" +
                "           \"https://www.w3.org/ns/activitystreams#Public\"]\n" +
                "  },\n" +
                "  \"published\": \"2015-02-10T15:04:55Z\",\n" +
                "  \"to\": [\"https://example.org/~john/\"],\n" +
                "  \"cc\": [\"https://example.com/~erik/followers\",\n" +
                "         \"https://www.w3.org/ns/activitystreams#Public\"]\n" +
                "}";

        // When
        Result result = client.postActivity("Alice", createNoteJson);

        // Then
        assertThat("Create activity failed.", statusOf(result), equalTo(CREATED));

        assertThat(result, hasLocationHeader());

        String locationUrl = result.headers().get(LOCATION);

        Result getByLocationResult = client.byLocationWithUserId(locationUrl, "Alice");

        assertThat(statusOf(getByLocationResult), equalTo(OK));
        JsonNode resultingCreateActivityJson = jsonOf(getByLocationResult);

        String idOfActivity = resultingCreateActivityJson.get("id").asText();
        assertThat(idOfActivity, equalTo(locationUrl));

        String createdObjectId = resultingCreateActivityJson.get("object").get("id").asText();
        assertThat(createdObjectId, notNullValue());

        String createdObjectAttributedTo = resultingCreateActivityJson.get("object").get("attributedTo").asText();
        assertThat(createdObjectAttributedTo, containsString("alice"));
    }

    @Test
    public void testImplicitCreateActivity() {
        // Given
        String createNoteJson = "{\n" +
                "  \"@context\": \"https://www.w3.org/ns/activitystreams\",\n" +
                "  \"type\": \"Note\",\n" +
                "  \"content\": \"This is a note\",\n" +
                "  \"published\": \"2015-02-10T15:04:55Z\",\n" +
                "  \"to\": [\"https://example.org/~john/\"],\n" +
                "  \"cc\": [\"https://example.com/~erik/followers\",\n" +
                "         \"https://www.w3.org/ns/activitystreams#Public\"]\n" +
                "}";

        // When
        Result result = client.postActivity("Alice", createNoteJson);

        // Then
        assertThat("Create activity failed.", statusOf(result), equalTo(CREATED));

        assertThat(result, hasLocationHeader());

        String locationUrl = result.headers().get(LOCATION);

        // Since note is also targeted for the Public collection.
        Result getByLocationResult = client.byLocation(locationUrl);

        assertThat(statusOf(getByLocationResult), equalTo(OK));
        JsonNode resultingCreateActivityJson = jsonOf(getByLocationResult);

        String idOfActivity = resultingCreateActivityJson.get("id").asText();
        assertThat(idOfActivity, equalTo(locationUrl));

        String createdObjectId = resultingCreateActivityJson.get("object").get("id").asText();
        assertThat(createdObjectId, notNullValue());

        String createdObjectAttributedTo = resultingCreateActivityJson.get("object").get("attributedTo").asText();
        assertThat(createdObjectAttributedTo, containsString("alice"));
    }

    @Test
    public void testUpdateActivity() {
        // Given
        String objectId = createAPublicNote();

        Result getByLocationResult = client.byLocation(objectId);
        JsonNode resultingCreateActivityJson = jsonOf(getByLocationResult);
        JsonNode noteObjectBeforeUpdate = resultingCreateActivityJson.get("object");

        String toBeforeUpdate = noteObjectBeforeUpdate.get("to").get(0).asText();
        String contentBeforeUpdate = noteObjectBeforeUpdate.get("content").asText();

        // When
        String actorId = routes.SocialController.getActor("alice").url();
        String updateContent = "{\n" +
                "  \"@context\": \"https://www.w3.org/ns/activitystreams\",\n" +
                "  \"summary\": \"Alice updated her note\",\n" +
                "  \"type\": \"Update\",\n" +
                "  \"actor\": \"" + actorId + "\",\n" +
                "  \"to\": [\"https://www.w3.org/ns/activitystreams#Public\"],\n" +
                "  \"object\": {\n" +
                "    \"id\": \"" + objectId + "\",\n" +
                "    \"type\": \"Note\",\n" +
                "    \"content\": \"This is a changed note\"\n" +
                "  }\n" +
                "}";

        Result result = client.updateActivity("Alice", updateContent);

        // Then
        assertThat(statusOf(result), equalTo(CREATED));
        assertThat(result, hasLocationHeader());

        String location = result.headers().get(LOCATION);
        result = client.byLocation(location);

        assertThat(statusOf(result), equalTo(OK));
        JsonNode updateActivityJson = jsonOf(result);

        String updatedNoteId = updateActivityJson.get("object").get("id").asText();
        result = client.byLocation(updatedNoteId);

        assertThat(statusOf(result), equalTo(OK));
        JsonNode updatedNoteJson = jsonOf(result);

        String toAfterUpdate = updatedNoteJson.get("to").asText();
        assertThat(toBeforeUpdate, equalTo(toAfterUpdate));

        String contentAfterUpdate = updatedNoteJson.get("content").asText();
        assertThat(contentBeforeUpdate, not(equalTo(contentAfterUpdate)));
    }

    @Test
    public void testDeleteActivity() {
        // Given
        // When
        // Then
        // TODO
        fail("Implement delete activity test.");
    }

    @Test
    public void testNotAllowed() {
        // Given
        // When
        // Then
        // TODO
        fail("Implement not allowed activity test.");
    }

    private void signupAlice() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUserId("alice");
        signupRequest.setNetwork("mockblockchain");
        signupRequest.setAccountPublic("mockpublic");
        signupRequest.setAccountSecret("mocksecret");
        signupRequest.setName("Actor Alice");
        signupRequest.setPreferredUserName("Alice");

        Result result = client.signup("alice", signupRequest);
        assertThat("Could not sign up Alice.", statusOf(result), equalTo(CREATED));
    }

    private String createAPublicNote() {
        String createNoteJson = "{\n" +
                "  \"@context\": \"https://www.w3.org/ns/activitystreams\",\n" +
                "  \"type\": \"Note\",\n" +
                "  \"content\": \"This is a note\",\n" +
                "  \"published\": \"2015-02-10T15:04:55Z\",\n" +
                "  \"to\": [\"https://example.org/~john/\"],\n" +
                "  \"cc\": [\"https://example.com/~erik/followers\",\n" +
                "         \"https://www.w3.org/ns/activitystreams#Public\"]\n" +
                "}";
        Result result = client.postActivity("Alice", createNoteJson);

        assertThat(statusOf(result), equalTo(CREATED));
        assertThat(result, hasLocationHeader());

        return result.headers().get(LOCATION);
    }
}
