package host.galactic.stellar.rest.requests.voting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.testutils.ValidationBaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreateVotingRequestFundingAccountSecretTest extends ValidationBaseTest {
    @Test
    public void testFundingAccountSecretBlank() {
        CreateVotingRequest nullFundingAccountRequest = makeCreateVotingRequestWithFundingAccountSecret(null);

        var violations = validator.validateProperty(nullFundingAccountRequest, "fundingAccountSecret");
        assertThat("Expected to have 2 violations for blank funding account, but there isn't.", violations, hasSize(2));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Funding account cannot be blank.", "Must be a valid stellar secret seed."));
    }

    @Test
    public void testFundingAccountSecretInvalid() {
        CreateVotingRequest invalidFundingAccountRequest = makeCreateVotingRequestWithFundingAccountSecret(
                "SBIH7SPTIV4P4PDMBFKLDU_NOT_VALID_H64GWMMHDEGHWNWZ7YGM6Y4");

        var violations = validator.validateProperty(invalidFundingAccountRequest, "fundingAccountSecret");
        assertThat("Expected to have 1 violation for invalid funding account, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Must be a valid stellar secret seed."));
    }

    private CreateVotingRequest makeCreateVotingRequestWithFundingAccountSecret(String value) {
        ObjectNode objectNode = readJsonFile("valid-voting-request.json");
        objectNode.put("fundingAccountSecret", value);

        return convertJsonNodeTo(CreateVotingRequest.class, objectNode);
    }
}
