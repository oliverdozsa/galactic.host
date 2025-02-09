package host.galactic.stellar.rest.requests.voting;

import host.galactic.testutils.ValidationTestsBase;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class AddVotersRequestTests extends ValidationTestsBase {
    @Test
    public void testEmailsIsNull() {
        var nullEmailsRequest = new AddVotersRequest(null);

        var violations = validator.validateProperty(nullEmailsRequest, "emails");
        assertThat("Expected to have no violation for null emails, but there is.", violations, hasSize(0));
    }

    @Test
    public void testEmailsIsEmpty() {
        var emptyEmailsRequest = new AddVotersRequest(new ArrayList<>());

        var violations = validator.validateProperty(emptyEmailsRequest, "emails");
        assertThat("Expected to have no violation for empty emails, but there is.", violations, hasSize(0));
    }

    @Test
    public void testValidEmails() {
        var emptyEmailsRequest = new AddVotersRequest(List.of("kate@galactic.pub", "leslie@galactic.pub"));

        var violations = validator.validateProperty(emptyEmailsRequest, "emails");
        assertThat("Expected to have no violation for valid emails, but there is.", violations, hasSize(0));
    }

    @Test
    public void testInvalidEmails() {
        var emptyEmailsRequest = new AddVotersRequest(List.of("kate@galactic.pub", "leslie!galactic.pub"));

        var violations = validator.validateProperty(emptyEmailsRequest, "emails");
        assertThat("Expected to have 1 violation for invalid emails, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Invalid email."));
    }
}
