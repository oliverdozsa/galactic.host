package host.galactic.stellar.rest;

import host.galactic.stellar.rest.requests.CreateVotingRequest;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class StellarVotingCreateValidationTest {
    @Inject
    Validator validator;

    @Test
    public void testValidationWorksThroughEndpoint() {
        fail("testValidationWorksThroughEndpoint() is not implemented!");
    }

    @Test
    public void testTooManyVoters() {
        fail("testTooManyVoters() is not implemented!");
    }

    @Test
    public void testInvalidTitle() {
        CreateVotingRequest titleEmptyVotingRequest = new CreateVotingRequest("");

        var violations = validator.validateProperty(titleEmptyVotingRequest, "title");
        assertThat("Expected to have violations for invalid title, but there isn't.", violations, not(empty()));

        var violationMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        assertThat(violationMessages, hasItems("Title cannot be blank", "Title length must be >= 2 and <= 1000"));
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
    public void testInvalidEmails() {
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
