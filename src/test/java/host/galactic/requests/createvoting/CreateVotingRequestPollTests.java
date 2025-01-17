package host.galactic.requests.createvoting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.stellar.rest.requests.CreateVotingRequest;
import host.galactic.testutils.ValidationTestsBase;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreateVotingRequestPollTests extends ValidationTestsBase {
    @Test
    public void testVotingRequestValidationWithInvalidPoll() {
        CreateVotingRequest invalidPollQuestionRequest = makeCreateVotingRequestWithPollTitle("a");

        var violations = validator.validate(invalidPollQuestionRequest);
        assertThat("Expected to have 1 violation for invalid poll question, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Question length must be >= 2 and <= 1000."));
    }

    private CreateVotingRequest makeCreateVotingRequestWithPollTitle(String value) {
        ObjectNode objectNode = readJsonFile("valid-voting-request.json");
        ObjectNode poll = (ObjectNode) objectNode.get("polls").get(0);
        poll.put("question", value);

        return convertJsonNodeTo(CreateVotingRequest.class, objectNode);
    }
}
