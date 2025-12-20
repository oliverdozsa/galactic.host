package host.galactic.stellar.rest.requests.voting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.testutils.ValidationBaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreatePollRequestQuestionTest extends ValidationBaseTest {
    @Test
    public void testQuestionIsNull() {
        CreatePollRequest nullQuestionPoll = makeCreatePollRequestWithQuestion(null);

        var violations = validator.validateProperty(nullQuestionPoll, "question");
        assertThat("Expected to have 1 violation for null question, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Question must not be blank."));
    }

    @Test
    public void testQuestionIsBlank() {
        CreatePollRequest blankQuestionPoll = makeCreatePollRequestWithQuestion(null);

        var violations = validator.validateProperty(blankQuestionPoll, "question");
        assertThat("Expected to have 1 violation for blank question, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Question must not be blank."));
    }

    @Test
    public void testQuestionIsTooShort() {
        CreatePollRequest tooShortQuestionPoll = makeCreatePollRequestWithQuestion("a");

        var violations = validator.validateProperty(tooShortQuestionPoll, "question");
        assertThat("Expected to have 1 violation for too short question, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Question length must be >= 2 and <= 1000."));
    }

    @Test
    public void testQuestionIsTooLong() {
        String tooLongQuestion = createTooLongQuestion();
        CreatePollRequest tooLongQuestionPoll = makeCreatePollRequestWithQuestion(tooLongQuestion);

        var violations = validator.validateProperty(tooLongQuestionPoll, "question");
        assertThat("Expected to have 1 violation for too long question, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Question length must be >= 2 and <= 1000."));
    }

    private CreatePollRequest makeCreatePollRequestWithQuestion(String value) {
        ObjectNode votingRequestJson = readJsonFile("valid-voting-request.json");
        ObjectNode pollJson = (ObjectNode) votingRequestJson.get("polls").get(0);

        pollJson.put("question", value);

        return convertJsonNodeTo(CreatePollRequest.class, pollJson);
    }

    private String createTooLongQuestion() {
        return generateRandomStringOfLength(1001);
    }
}
