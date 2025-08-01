package host.galactic.stellar.rest.requests.voting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.testutils.ValidationTestsBase;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreateVotingTitleTests extends ValidationTestsBase {
    @Test
    public void testBlankTitle() {
        CreateVotingRequest blankTitleRequest = makeCreateVotingRequestWithTitle("");

        var violations = validator.validateProperty(blankTitleRequest, "title");
        assertThat("Expected to have 2 violations for blank title, but there isn't.", violations, hasSize(2));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Title cannot be blank.", "Title length must be >= 2 and <= 1000."));
    }

    @Test
    public void testTooShortTitle() {
        CreateVotingRequest tooShortTitleRequest = makeCreateVotingRequestWithTitle("a");

        var violations = validator.validateProperty(tooShortTitleRequest, "title");
        assertThat("Expected to have 1 violation for too short title.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Title length must be >= 2 and <= 1000."));
    }

    @Test
    public void testTooLongTitle() {
        String tooLongTitle = createTooLongTitle();
        CreateVotingRequest tooLongTitleRequest = makeCreateVotingRequestWithTitle(tooLongTitle);

        var violations = validator.validateProperty(tooLongTitleRequest, "title");
        assertThat("Expected to have 1 violation for too long title.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Title length must be >= 2 and <= 1000."));
    }

    private String createTooLongTitle() {
        return generateRandomStringOfLength(1001);
    }

    private CreateVotingRequest makeCreateVotingRequestWithTitle(String value) {
        ObjectNode objectNode = readJsonFile("valid-voting-request.json");
        objectNode.put("title", value);

        return convertJsonNodeTo(CreateVotingRequest.class, objectNode);
    }
}
