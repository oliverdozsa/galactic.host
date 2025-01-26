package host.galactic.stellar.rest.requests.createvoting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.testutils.ValidationTestsBase;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreateVotingRequestBallotTypeTests extends ValidationTestsBase {
    @Test
    public void testNullBallotType() {
        CreateVotingRequest nullBallotTypeRequest = makeCreateVotingRequestWithBallotType(null);

        var violations = validator.validateProperty(nullBallotTypeRequest, "ballotType");
        assertThat("Expected to have 0 violation for null ballot type, but there is.", violations, hasSize(0));
    }

    private CreateVotingRequest makeCreateVotingRequestWithBallotType(CreateVotingRequest.BallotType value) {
        ObjectNode objectNode = readJsonFile("valid-voting-request.json");

        if (value == null) {
            objectNode.put("ballotType", (String) null);
        } else {
            objectNode.put("ballotType", value.name());
        }

        return convertJsonNodeTo(CreateVotingRequest.class, objectNode);
    }
}
