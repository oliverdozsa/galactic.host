package host.galactic.stellar.rest;

import host.galactic.stellar.StellarTest;
import host.galactic.stellar.rest.requests.commission.CommissionInitRequest;
import host.galactic.stellar.rest.responses.commission.CommissionInitResponse;
import host.galactic.testutils.AuthForTest;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
public class StellarCommissionInitRestTest {
    @Inject
    private StellarTest test;

    @Inject
    private AuthForTest auth;

    @Test
    public void testInitSession() {
        Log.info("[START TEST]: testInitSession()");

        var votingId = initializeAVotingWithParticipant("charlie");

        var withAccessToken = auth.loginAs("charlie");

        var initRequest = new CommissionInitRequest(votingId);

        var response = given()
                .auth().oauth2(withAccessToken)
                .contentType(ContentType.JSON)
                .body(initRequest)
                .when()
                .post(test.getRest().getCommission().getUrl() + "/initsession")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body()
                .as(CommissionInitResponse.class);

        assertThat(response.publicKey(), notNullValue());

        Log.info("[  END TEST]: testInitSession()");
    }

    @Test
    public void testInitSessionVotingDoesNotExists() {
        fail("Implement testInitSessionVotingDoesNotExists()");
    }

    @Test
    public void testInitSessionUserIsNotParticipant() {
        fail("Implement testInitSessionUserIsNotParticipant()");
    }

    @Test
    public void testInitSessionVotingIsNotInitializedFully() {
        fail("Implement testInitSessionVotingIsNotInitializedFully()");
    }

    @Test
    public void testInitSessionUserIsNotAuthorizedToInit() {
        fail("Implement testInitSessionUserIsNotParticipant()");
    }

    private Long initializeAVotingWithParticipant(String voter) {
//        var votingId = createAVotingAs("alice");
//        addVoterAsParticipantTo(votingId, voter, "alice");
//
//        waitForChannelAccountsToBeCreatedFor(votingId);
//        waitForAssetAccountsToBeCreatedFor(votingId);

//        return votingId;
        return 0L;
    }
}
