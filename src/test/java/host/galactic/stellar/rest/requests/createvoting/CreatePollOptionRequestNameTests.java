package host.galactic.stellar.rest.requests.createvoting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.testutils.ValidationTestsBase;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreatePollOptionRequestNameTests extends ValidationTestsBase {
    @Test
    public void testNameIsNull() {
        CreatePollOptionRequest nullNameRequest = makeCreatePollOptionRequestWithName(null);

        var violations = validator.validateProperty(nullNameRequest, "name");
        assertThat("Expected to have 1 violation for null name.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Name cannot be blank."));
    }

    @Test
    public void testNameIsBlank() {
        CreatePollOptionRequest blankNameRequest = makeCreatePollOptionRequestWithName("");

        var violations = validator.validateProperty(blankNameRequest, "name");
        assertThat("Expected to have 2 violations for blank name.", violations, hasSize(2));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Name cannot be blank.", "Name length must be >= 2 and <= 300."));
    }

    @Test
    public void testNameIsTooLong() {
        String tooLongName = createTooLongName();
        CreatePollOptionRequest blankNameRequest = makeCreatePollOptionRequestWithName(tooLongName);

        var violations = validator.validateProperty(blankNameRequest, "name");
        assertThat("Expected to have 1 violation for too long name.", violations, hasSize(1));

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

    private String createTooLongName() {
        return generateRandomStringOfLength(301);
    }
}
