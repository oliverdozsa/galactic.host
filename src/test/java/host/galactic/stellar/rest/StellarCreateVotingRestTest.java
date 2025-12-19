package host.galactic.stellar.rest;

import host.galactic.stellar.StellarTest;
import host.galactic.stellar.operations.MockStellarOperations;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import host.galactic.stellar.rest.responses.voting.VotingPollOptionResponse;
import host.galactic.stellar.rest.responses.voting.VotingResponse;
import host.galactic.testutils.AuthForTest;
import host.galactic.testutils.JsonUtils;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class StellarCreateVotingRestTest {
    @Inject
    private AuthForTest auth;

    @Inject
    private StellarTest test;

    @Test
    public void testCreateVoting() {
        Log.info("[START TEST]: testCreateVoting()");

        var createRequest = test.rest.voting.makeCreateRequest();

        String withAccessToken = auth.loginAs("alice");
        String locationHeader = given()
                .auth().oauth2(withAccessToken)
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post(test.rest.voting.url)
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        assertThat(locationHeader, not(blankOrNullString()));

        VotingResponse votingResponse = given()
                .auth().oauth2(withAccessToken)
                .get(locationHeader)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(VotingResponse.class);

        assertThat(votingResponse.title(), equalTo(createRequest.title()));

        var polls = votingResponse.polls();
        assertThat(polls.get(0).question(), equalTo("What is your favorite chocolate?"));

        var pollOptionNames = polls.get(0).pollOptions()
                .stream()
                .map(VotingPollOptionResponse::name)
                .toList();
        assertThat(pollOptionNames, hasItems("White", "Milk", "Dark"));

        Log.info("[  END TEST]: testCreateVoting()\n\n");
    }

    @Test
    public void testCreateInvalidVoting() {
        Log.info("[START TEST]: testCreateInvalidVoting()");

        var invalidCreateRequest = makeInvalidCreateVotingRequest();

        String withAccessToken = auth.loginAs("alice");
        given()
                .auth().oauth2(withAccessToken)
                .contentType(ContentType.JSON)
                .body(invalidCreateRequest)
                .when()
                .post(test.rest.voting.url)
                .then()
                .statusCode(400);

        Log.info("[  END TEST]: testCreateInvalidVoting()\n\n");
    }

    @Test
    public void testNotExistingVoting() {
        Log.info("[START TEST]: testNotExistingVoting()");

        String withAccessToken = auth.loginAs("alice");
        given()
                .auth().oauth2(withAccessToken)
                .when()
                .get(test.rest.voting.url + "/42")
                .then()
                .statusCode(404);

        Log.info("[  END TEST]: testNotExistingVoting()\n\n");
    }

    @Test
    public void testFailedToDeductCostWhileCreatingVoting() {
        Log.info("[START TEST]: testFailedToDeductCostWhileCreatingVoting()");

        var createRequest = test.rest.voting.makeCreateRequest();

        MockStellarOperations.failTransferXlm();

        String withAccessToken = auth.loginAs("alice");
        given()
                .auth().oauth2(withAccessToken)
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post(test.rest.voting.url)
                .then()
                .statusCode(500);

        Log.info("[  END TEST]: testFailedToDeductCostWhileCreatingVoting()\n\n");
    }

    @AfterAll
    public static void tearDown() {
        MockStellarOperations.succeedTransferXlm();
    }

    private CreateVotingRequest makeInvalidCreateVotingRequest() {
        var votingRequestJson = JsonUtils.readJsonFile("valid-voting-request.json");
        votingRequestJson.put("title", "a");
        return JsonUtils.convertJsonNodeTo(CreateVotingRequest.class, votingRequestJson);
    }
}
