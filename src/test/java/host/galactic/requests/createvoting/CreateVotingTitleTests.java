package host.galactic.requests.createvoting;

import host.galactic.stellar.rest.requests.CreateVotingRequest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.StringJoiner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreateVotingTitleTests extends ValidationTestsBase {
    @Test
    public void testBlankTitle() {
        CreateVotingRequest blankTitleRequest = new CreateVotingRequest("", 0);

        var violations = validator.validateProperty(blankTitleRequest, "title");
        assertThat("Expected to have 2 violations for blank title, but there isn't.", violations, hasSize(2));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Title cannot be blank", "Title length must be >= 2 and <= 1000"));
    }

    @Test
    public void testTooShortTitle() {
        CreateVotingRequest tooShortTitleRequest = new CreateVotingRequest("a", 0);

        var violations = validator.validateProperty(tooShortTitleRequest, "title");
        assertThat("Expected to have 1 violation for too short title.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Title length must be >= 2 and <= 1000"));
    }

    @Test
    public void testTooLongTitle() {
        String tooLongTitle = createTooLongTitle();
        CreateVotingRequest tooLongTitleRequest = new CreateVotingRequest(tooLongTitle, 0);

        var violations = validator.validateProperty(tooLongTitleRequest, "title");
        assertThat("Expected to have 1 violation for too long title.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Title length must be >= 2 and <= 1000"));
    }

    private String createTooLongTitle() {
        StringJoiner titleJoiner = new StringJoiner("");
        for(int i = 0; i < 1001; i++) {
            titleJoiner.add("a");
        }

        return titleJoiner.toString();
    }
}
