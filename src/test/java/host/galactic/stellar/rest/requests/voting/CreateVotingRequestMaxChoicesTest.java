package host.galactic.stellar.rest.requests.voting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.testutils.ValidationTestsBase;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class CreateVotingRequestMaxChoicesTest  extends ValidationTestsBase {
    @Test
    public void testBallotTypeIsNotMultiChoiceAndMaxChoicesIsNull() {
        CreateVotingRequest nullMaxChoicesNotMultiChoiceRequest = makeCreateVotingRequestWithMaxChoices(null, CreateVotingRequest.BallotType.MULTI_POLL);

        var violations = validator.validate(nullMaxChoicesNotMultiChoiceRequest);
        assertThat("Expected to have 0 violation for null max choices given ballot type is not multi choice, but there is.", violations, hasSize(0));
    }

    @Test
    public void testBallotTypeIsMultiChoiceAndMaxChoicesIsNull() {
        CreateVotingRequest nullMaxChoicesMultiChoiceRequest = makeCreateVotingRequestWithMaxChoices(null, CreateVotingRequest.BallotType.MULTI_CHOICE);

        var violations = validator.validate(nullMaxChoicesMultiChoiceRequest);
        assertThat("Expected to have 1 violation for null max choices given ballot type is multi choice, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Max choices must be >= 1 if ballot type is MULTI_CHOICE."));
    }

    @Test
    public void testBallotTypeIsMultiChoiceAndMaxChoicesIsZero() {
        CreateVotingRequest zeroMaxChoicesMultiChoiceRequest = makeCreateVotingRequestWithMaxChoices(0, CreateVotingRequest.BallotType.MULTI_CHOICE);

        var violations = validator.validate(zeroMaxChoicesMultiChoiceRequest);
        assertThat("Expected to have 1 violation for 0 max choices given ballot type is multi choice, but there isn't.", violations, hasSize(1));

        var violationMessages = extractViolationMessages(violations);
        assertThat(violationMessages, hasItems("Max choices must be >= 1 if ballot type is MULTI_CHOICE."));
    }

    @Test
    public void testBallotTypeIsMultiChoiceAndMaxChoicesIsValid() {
        CreateVotingRequest zeroMaxChoicesMultiChoiceRequest = makeCreateVotingRequestWithMaxChoices(2, CreateVotingRequest.BallotType.MULTI_CHOICE);

        var violations = validator.validate(zeroMaxChoicesMultiChoiceRequest);
        assertThat("Expected to have 0 violation for valid max choices given ballot type is multi choice, but there is.", violations, hasSize(0));
    }

    private CreateVotingRequest makeCreateVotingRequestWithMaxChoices(Integer value, CreateVotingRequest.BallotType ballotType) {
        ObjectNode objectNode = readJsonFile("valid-voting-request.json");
        objectNode.put("maxChoices", value);
        objectNode.put("ballotType", ballotType.name());

        return convertJsonNodeTo(CreateVotingRequest.class, objectNode);
    }
}
