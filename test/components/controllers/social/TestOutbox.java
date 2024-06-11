package components.controllers.social;

import com.fasterxml.jackson.databind.JsonNode;
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
        String createNoteJson = "{\n" +
                "  \"@context\": \"https://www.w3.org/ns/activitystreams\",\n" +
                "  \"type\": \"Create\",\n" +
                "  \"actor\": \"https://example.net/~mallory\",\n" +
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
        JsonNode resultingCreateActivityJson =  jsonOf(getByLocationResult);

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
        // When
        // Then
        // TODO
        fail("Implement implicit create activity test.");
    }

    @Test
    public void testUpdateActivity() {
        // Given
        // When
        // Then
        // TODO
        fail("Implement update activity test.");
    }

    @Test
    public void testPartialUpdateActivity() {
        // Given
        // When
        // Then
        // TODO
        fail("Implement partial update activity test.");
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
}
