package host.galactic.stellar.rest.requests.commission;

import host.galactic.testutils.ValidationBaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CommissionCreateTransactionRequestTest extends ValidationBaseTest {
    @Test
    public void testValidCommissionCreateTransactionRequest() {
        var request = new CommissionCreateTransactionRequest("a|b", "abcd");

        var violations = validator.validateProperty(request, "message");
        assertThat("Expected no violation for valid message in transaction request!", violations, hasSize(0));

        violations = validator.validateProperty(request, "revealedSignatureBase64");
        assertThat("Expected no violation for valid signature in transaction request!", violations, hasSize(0));
    }

    @Test
    public void testInvalidMessageInCommissionCreateTransactionRequest() {
        // TODO
    }

    @Test
    public void testInvalidSignatureInCommissionCreateTransactionRequest() {

    }
}
