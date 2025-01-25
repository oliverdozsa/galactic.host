package host.galactic.stellar.rest.requests.createvoting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.testutils.ValidationTestsBase;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreateVotingVisibilityTests extends ValidationTestsBase {
    @Test
    public void testNullVisibility() {
        CreateVotingRequest visibilityNullRequest = makeCreateVotingRequestWithVisibility(null);

        var violations = validator.validateProperty(visibilityNullRequest, "visibility");
        assertThat("Expected to have 1 violation for null visibility, but there isn't.", violations, hasSize(1));
    }

    @Test
    public void testValidVisibility() {
        CreateVotingRequest visibilityNullRequest = makeCreateVotingRequestWithVisibility(CreateVotingRequest.Visibility.UNLISTED);

        var violations = validator.validateProperty(visibilityNullRequest, "visibility");
        assertThat("Expected to have 0 violation for valid visibility, but there is.", violations, hasSize(0));
    }

    private CreateVotingRequest makeCreateVotingRequestWithVisibility(CreateVotingRequest.Visibility value) {
        ObjectNode objectNode = readJsonFile("valid-voting-request.json");
        if(value == null) {
            objectNode.put("visibility", (String) null);
        } else {
            objectNode.put("visibility", value.name());
        }

        return convertJsonNodeTo(CreateVotingRequest.class, objectNode);
    }
}
