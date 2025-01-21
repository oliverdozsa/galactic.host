package host.galactic.requests.createvoting;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.stellar.rest.requests.CreatePollOptionRequest;
import host.galactic.stellar.rest.requests.CreatePollRequest;
import host.galactic.testutils.ValidationTestsBase;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreatePollRequestPollOptionValidationTest extends ValidationTestsBase {
    @Test
    public void testTooFewPollOptions() {
        CreatePollRequest tooFewOptionsRequest = makeCreatePollOptionRequestWithTooFewOptions();

        var violations = validator.validateProperty(tooFewOptionsRequest, "options");
        assertThat("Expected to have 1 violation for too few poll options.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Options length must be >= 2 and <= 99."));
    }

    @Test
    public void testTooShortPollOptionName() {
        CreatePollOptionRequest tooShortPollOptionName = makeCreatePollOptionRequestWithName("a");

        var violations = validator.validate(tooShortPollOptionName);
        assertThat("Expected to have 1 violation for too short poll option name.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Name length must be >= 2 and <= 300."));
    }

    private CreatePollOptionRequest makeCreatePollOptionRequestWithName(String value) {
        ObjectNode votingRequestJson = readJsonFile("valid-voting-request.json");
        ObjectNode pollJson = (ObjectNode) votingRequestJson.get("polls").get(0);
        ObjectNode optionJson = (ObjectNode) pollJson.get("options").get(0);

        optionJson.put("name", value);

        return convertJsonNodeTo(CreatePollOptionRequest.class, optionJson);
    }

    private CreatePollRequest makeCreatePollOptionRequestWithTooFewOptions() {
        ObjectNode votingRequestJson = readJsonFile("valid-voting-request.json");
        ObjectNode pollJson = (ObjectNode) votingRequestJson.get("polls").get(0);
        ObjectNode optionJson = (ObjectNode) pollJson.get("options").get(0);

        ArrayNode arrayNode = createArrayNode();
        arrayNode.add(optionJson);
        pollJson.set("options", arrayNode);

        return convertJsonNodeTo(CreatePollRequest.class, pollJson);
    }
}
