package host.galactic.stellar.rest.requests.voting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.testutils.ValidationBaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreateVotingDescriptionTest extends ValidationBaseTest {
    @Test
    public void testNullDescription() {
        CreateVotingRequest nullDescriptionRequest = makeCreateVotingRequestWithDescription(null);

        var violations = validator.validateProperty(nullDescriptionRequest, "description");
        assertThat("Expected to have no violation for null description, but there is.", violations, hasSize(0));
    }

    @Test
    public void testTooShortDescription() {
        CreateVotingRequest tooShortDescriptionRequest = makeCreateVotingRequestWithDescription("d");

        var violations = validator.validateProperty(tooShortDescriptionRequest, "description");
        assertThat("Expected to have 1 violation for too short description, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Description length must be >= 2 and <= 1000."));
    }

    @Test
    public void testTooLongDescription() {
        String tooLongDescription = createTooLongDescription();
        CreateVotingRequest tooLongDescriptionRequest = makeCreateVotingRequestWithDescription(tooLongDescription);

        var violations = validator.validateProperty(tooLongDescriptionRequest, "description");
        assertThat("Expected to have 1 violation for too long description, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Description length must be >= 2 and <= 1000."));
    }

    private CreateVotingRequest makeCreateVotingRequestWithDescription(String value) {
        ObjectNode objectNode = readJsonFile("valid-voting-request.json");
        objectNode.put("description", value);

        return convertJsonNodeTo(CreateVotingRequest.class, objectNode);
    }

    private String createTooLongDescription() {
        return generateRandomStringOfLength(1001);
    }
}
