package host.galactic.stellar.rest.requests.createvoting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.testutils.ValidationTestsBase;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreatePollRequestDescriptionTests extends ValidationTestsBase {
    @Test
    public void testDescriptionIsNull() {
        CreatePollRequest nullDescriptionRequest = makeCreatePollRequestWithDescription(null);

        var violations = validator.validateProperty(nullDescriptionRequest, "description");
        assertThat("Expected to have no violation for null description, but there is.", violations, hasSize(0));
    }

    @Test
    public void testDescriptionIsTooLong() {
        String tooLongDescription = createTooLongDescription();
        CreatePollRequest tooLongDescriptionRequest = makeCreatePollRequestWithDescription(tooLongDescription);

        var violations = validator.validateProperty(tooLongDescriptionRequest, "description");
        assertThat("Expected to have 1 violation for too long description, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Description length must be <= 1000."));
    }

    private CreatePollRequest makeCreatePollRequestWithDescription(String value) {
        ObjectNode votingRequestJson = readJsonFile("valid-voting-request.json");
        ObjectNode pollJson = (ObjectNode) votingRequestJson.get("polls").get(0);

        pollJson.put("description", value);

        return convertJsonNodeTo(CreatePollRequest.class, pollJson);
    }

    private String createTooLongDescription() {
        return generateRandomStringOfLength(1001);
    }
}
