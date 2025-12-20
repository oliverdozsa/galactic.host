package host.galactic.stellar.rest.requests.commission;

import host.galactic.testutils.ValidationBaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CommissionInitRequestTest extends ValidationBaseTest {
    @Test
    public void testValidRequest() {
        var request = new CommissionInitRequest(42L);
        var violations = validator.validateProperty(request, "votingId");

        assertThat("Expected no violation for valid commission init request.", violations, hasSize(0));
    }

    @Test
    public void testInvalidRequest() {
        var request = new CommissionInitRequest(null);
        var violations = validator.validateProperty(request, "votingId");

        assertThat("Expected to have 1 violation for invalid commission init request.", violations, hasSize(1));
    }
}
