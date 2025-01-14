package host.galactic.stellar.rest;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class StellarVotingCreateValidationTest {
    @Test
    public void testValidationWorksThroughEndpoint() {
        fail("testValidationWorksThroughEndpoint() is not implemented!");
    }

    @Test
    public void testInvalidTokenId() {
        fail("testInvalidTokenId() is not implemented!");
    }

    @Test
    public void testInvalidStartDate_EndDate() {
        fail("testInvalidStartDate_EndDate() is not implemented!");
    }

    @Test
    public void testInvalidVisibility() {
        fail("testInvalidVisibility() is not implemented!");
    }

    @Test
    public void testInvalidFundingAccount() {
        fail("testInvalidVisibility() is not implemented!");
    }

    @Test
    public void testInsufficientFundsOnFundingAccount() {
        fail("testInsufficientFundsOnFundingAccount() is not implemented!");
    }

    @Test
    public void testInvalidEncryptedUntil() {
        fail("testInvalidEncryptedUntil() is not implemented!");
    }

    @Test
    public void testInvalidBallotType() {
        fail("testInvalidBallotType() is not implemented!");
    }

    @Test
    public void testInvalidOrganizer() {
        fail("testInvalidBallotType() is not implemented!");
    }

    @Test
    public void testPollsAreValidated() {
        fail("testPollsAreValidated() is not implemented!");
    }
}
