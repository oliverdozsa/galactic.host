package host.galactic.stellar.rest.requests.commission;

import host.galactic.testutils.ValidationBaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CommissionSignEnvelopeRequestTest extends ValidationBaseTest {
    @Test
    public void testValidCommissionSignEnvelopeRequest() {
        var request = new CommissionSignEnvelopeRequest("some envelope");
        var violations = validator.validateProperty(request, "envelopeBase64");

        assertThat("Expected no violations for valid sign envelope request!", violations, hasSize(0));
    }

    @Test
    public void testInvalidCommissionSignEnvelopeRequest() {
        var request = new CommissionSignEnvelopeRequest("");
        var violations = validator.validateProperty(request, "envelopeBase64");

        assertThat("Expected 1 violation for invalid sign envelope request!", violations, hasSize(1));
    }
}
