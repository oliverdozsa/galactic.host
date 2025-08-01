package host.galactic.stellar.rest.requests.voting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.testutils.ValidationTestsBase;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreateVotingMaxVotersTests extends ValidationTestsBase {
    @Test
    public void testTooManyMaxVoters() {
        CreateVotingRequest tooManyMaxVotersRequest = makeCreateVotingRequestWithMaxVoters(501);

        var violations = validator.validateProperty(tooManyMaxVotersRequest, "maxVoters");
        assertThat("Expected to have 1 violation for too many max voters, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("There can be at most 500 voters per voting."));
    }

    @Test
    public void testTooFewMaxVoters() {
        CreateVotingRequest tooFewMaxVotersRequest = makeCreateVotingRequestWithMaxVoters(1);

        var violations = validator.validateProperty(tooFewMaxVotersRequest, "maxVoters");
        assertThat("Expected to have 1 violation for too few max voters, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("There must be at least 2 voters."));
    }

    @Test
    public void testNoMaxVotersGiven() {
        CreateVotingRequest maxVotersNotDefined = makeCreateVotingRequestWithMaxVoters(null);

        var violations = validator.validateProperty(maxVotersNotDefined, "maxVoters");
        assertThat("Expected to have 1 violation for not defined max voters, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Max voters must not be null."));
    }

    private CreateVotingRequest makeCreateVotingRequestWithMaxVoters(Integer value) {
        ObjectNode objectNode = readJsonFile("valid-voting-request.json");
        objectNode.put("maxVoters", value);

        return convertJsonNodeTo(CreateVotingRequest.class, objectNode);
    }
}
