package host.galactic.stellar.rest.requests.voting;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.testutils.ValidationBaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreatePollRequestPollOptionValidationTestTest extends ValidationBaseTest {
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

    @Test
    public void testPollOptionsWithSameCodes() {
        CreatePollRequest sameOptionCodesRequest = makeCreatePollRequestWithDuplicateCodes();

        var violations = validator.validate(sameOptionCodesRequest);
        assertThat("Expected to have 1 violation for duplicate option codes.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Option codes must be unique."));
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

    private CreatePollRequest makeCreatePollRequestWithDuplicateCodes() {
        ObjectNode votingRequestJson = readJsonFile("valid-voting-request.json");
        ObjectNode pollJson = (ObjectNode) votingRequestJson.get("polls").get(0);
        ObjectNode optionJson = (ObjectNode) pollJson.get("options").get(1);

        optionJson.put("code", 1);

        return convertJsonNodeTo(CreatePollRequest.class, pollJson);
    }
}
