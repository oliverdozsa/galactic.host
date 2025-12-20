package host.galactic.stellar.rest.requests.voting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.testutils.ValidationBaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreateVotingTokenIdTest extends ValidationBaseTest {
    @Test
    public void testNullTokenId() {
        CreateVotingRequest blankTokenIdRequest = makeCreateVotingRequestWithTokenId(null);

        var violations = validator.validateProperty(blankTokenIdRequest, "tokenId");
        assertThat("Expected to have 1 violation for null token ID, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Token ID cannot be blank."));
    }

    @Test
    public void testBlankTokenId() {
        CreateVotingRequest nullTokenIdRequest = makeCreateVotingRequestWithTokenId("");

        var violations = validator.validateProperty(nullTokenIdRequest, "tokenId");
        assertThat("Expected to have 3 violations for blank token ID, but there isn't.", violations, hasSize(3));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Token ID cannot be blank.", "Token ID's length must be >= 2 and <= 8.",
                "Token ID must consists of number and / or lowercase letters."));
    }

    @Test
    public void testTooShortTokenId() {
        CreateVotingRequest tooShortTokenIdRequest = makeCreateVotingRequestWithTokenId("t");

        var violations = validator.validateProperty(tooShortTokenIdRequest, "tokenId");
        assertThat("Expected to have 1 violation for too short token ID, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Token ID's length must be >= 2 and <= 8."));
    }

    @Test
    public void testTooLongTokenId() {
        CreateVotingRequest tooLongTokenIdRequest = makeCreateVotingRequestWithTokenId("toolongtokenid");

        var violations = validator.validateProperty(tooLongTokenIdRequest, "tokenId");
        assertThat("Expected to have 1 violation for too long token ID, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Token ID's length must be >= 2 and <= 8."));
    }

    @Test
    public void testInvalidTokenId() {
        CreateVotingRequest invalidTokenIdRequest = makeCreateVotingRequestWithTokenId("inv#lid");

        var violations = validator.validateProperty(invalidTokenIdRequest, "tokenId");
        assertThat("Expected to have 1 violation for invalid long token ID, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Token ID must consists of number and / or lowercase letters."));
    }

    private CreateVotingRequest makeCreateVotingRequestWithTokenId(String value) {
        ObjectNode objectNode = readJsonFile("valid-voting-request.json");
        objectNode.put("tokenId", value);

        return convertJsonNodeTo(CreateVotingRequest.class, objectNode);
    }
}
