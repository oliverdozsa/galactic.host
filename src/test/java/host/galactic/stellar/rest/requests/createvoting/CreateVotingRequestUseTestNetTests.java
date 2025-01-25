package host.galactic.stellar.rest.requests.createvoting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.testutils.ValidationTestsBase;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreateVotingRequestUseTestNetTests extends ValidationTestsBase {
    @Test
    public void testUseTestNetIsNull() {
        CreateVotingRequest tooManyMaxVotersRequest = makeCreateVotingRequestWithUseTestNet(null);

        var violations = validator.validateProperty(tooManyMaxVotersRequest, "useTestNet");
        assertThat("Expected to have 0 violation for null use test ned, but there i.", violations, hasSize(0));
    }

    private CreateVotingRequest makeCreateVotingRequestWithUseTestNet(Boolean value) {
        ObjectNode objectNode = readJsonFile("valid-voting-request.json");
        objectNode.put("useTestNet", value);

        return convertJsonNodeTo(CreateVotingRequest.class, objectNode);
    }
}
