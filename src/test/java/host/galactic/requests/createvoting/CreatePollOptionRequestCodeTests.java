package host.galactic.requests.createvoting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.stellar.rest.requests.CreatePollOptionRequest;
import host.galactic.testutils.ValidationTestsBase;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreatePollOptionRequestCodeTests extends ValidationTestsBase {
    @Test
    public void testCodeIsNull() {
        CreatePollOptionRequest nullCodeRequest = makeCreatePollOptionRequestWithCode(null);

        var violations = validator.validateProperty(nullCodeRequest, "code");
        assertThat("Expected to have 1 violation for null code.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Code cannot be null."));
    }

    @Test
    public void testCodeIsTooSmall() {
        CreatePollOptionRequest nullCodeRequest = makeCreatePollOptionRequestWithCode(0);

        var violations = validator.validateProperty(nullCodeRequest, "code");
        assertThat("Expected to have 1 violation for too small code.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Code value must be >= 1."));
    }

    @Test
    public void testCodeIsTooBig() {
        CreatePollOptionRequest nullCodeRequest = makeCreatePollOptionRequestWithCode(100);

        var violations = validator.validateProperty(nullCodeRequest, "code");
        assertThat("Expected to have 1 violation for too big code.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Code value must be <= 99."));
    }

    private CreatePollOptionRequest makeCreatePollOptionRequestWithCode(Integer value) {
        ObjectNode votingRequestJson = readJsonFile("valid-voting-request.json");
        ObjectNode pollJson = (ObjectNode) votingRequestJson.get("polls").get(0);
        ObjectNode optionJson = (ObjectNode) pollJson.get("options").get(0);

        optionJson.put("code", value);

        return convertJsonNodeTo(CreatePollOptionRequest.class, optionJson);
    }
}
