package host.galactic.stellar.rest.requests.createvoting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.testutils.ValidationTestsBase;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreateVotingDatesValidationTests extends ValidationTestsBase {
    @Test
    public void testEncryptedUntilIsInThePast() {
        CreateVotingRequest encryptedUntilInPastRequest =
                makeCreateVotingRequestWithEncryptedUntil("1942-04-20T04:20:42.0Z");

        var violations = validator.validateProperty(encryptedUntilInPastRequest, "dates.encryptedUntil");
        assertThat("Expected to have 1 violation for encrypted until in the past, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Encrypted until must be in the future."));
    }

    @Test
    public void testStartDateIsNull() {
        CreateVotingRequest startDateMissingRequest = makeCreateVotingRequestWithStartDate(null);

        var violations = validator.validateProperty(startDateMissingRequest, "dates.startDate");
        assertThat("Expected to have 1 violation for missing start date, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Start date must not be null."));
    }

    @Test
    public void testEndDateIsNull() {
        CreateVotingRequest endDateMissingRequest = makeCreateVotingRequestWithEndDate(null);

        var violations = validator.validateProperty(endDateMissingRequest, "dates.endDate");
        assertThat("Expected to have 1 violation for missing end date, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("End date must not be null."));
    }

    @Test
    public void testStartDateIsLaterThanEndDate() {
        CreateVotingRequest startDateEndDateNotConsistentRequest =
                makeCreateVotingRequestWithStartEndDates("2100-04-20T04:20:42.0Z", "2100-02-02T04:20:42.0Z");

        var violations = validator.validate(startDateEndDateNotConsistentRequest);
        assertThat("Expected to have 1 violation for inconsistent start - end dates, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Start date must be before end date."));
    }

    @Test
    public void testEndDateIsInThePast() {
        CreateVotingRequest endDateMissingRequest = makeCreateVotingRequestWithEndDate("1942-04-20T04:20:42.0Z");

        var violations = validator.validateProperty(endDateMissingRequest, "dates.endDate");
        assertThat("Expected to have 1 violation for end date in the past, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("End date must be in the future."));
    }

    private CreateVotingRequest makeCreateVotingRequestWithEncryptedUntil(String value) {
        ObjectNode objectNode = readJsonFile("valid-voting-request.json");
        ObjectNode datesJson = (ObjectNode) objectNode.get("dates");
        datesJson.put("encryptedUntil", value);

        return convertJsonNodeTo(CreateVotingRequest.class, objectNode);
    }

    private CreateVotingRequest makeCreateVotingRequestWithStartDate(String value) {
        ObjectNode objectNode = readJsonFile("valid-voting-request.json");
        ObjectNode datesJson = (ObjectNode) objectNode.get("dates");
        datesJson.put("startDate", value);

        return convertJsonNodeTo(CreateVotingRequest.class, objectNode);
    }

    private CreateVotingRequest makeCreateVotingRequestWithEndDate(String value) {
        ObjectNode objectNode = readJsonFile("valid-voting-request.json");
        ObjectNode datesJson = (ObjectNode) objectNode.get("dates");
        datesJson.put("endDate", value);

        return convertJsonNodeTo(CreateVotingRequest.class, objectNode);
    }

    private CreateVotingRequest makeCreateVotingRequestWithStartEndDates(String startDate, String endDate) {
        ObjectNode objectNode = readJsonFile("valid-voting-request.json");
        ObjectNode datesJson = (ObjectNode) objectNode.get("dates");
        datesJson.put("endDate", endDate);
        datesJson.put("startDate", startDate);

        return convertJsonNodeTo(CreateVotingRequest.class, objectNode);
    }
}
