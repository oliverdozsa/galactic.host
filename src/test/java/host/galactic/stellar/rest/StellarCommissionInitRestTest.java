package host.galactic.stellar.rest;

import host.galactic.stellar.rest.requests.commission.CommissionInitRequest;
import host.galactic.stellar.rest.responses.commission.CommissionInitResponse;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
public class StellarCommissionInitRestTest extends StellarRestTestBase {
    @Test
    public void testInitSession() {
        Log.info("[START TEST]: testInitSession()");

        var votingId = createAVotingAs("alice");
        addVoterAsParticipantTo(votingId, "charlie", "alice");

        var withAccessToken = authForTest.loginAs("charlie");

        var initRequest = new CommissionInitRequest();

        var response = given()
                .auth().oauth2(withAccessToken)
                .contentType(ContentType.JSON)
                .body(initRequest)
                .when()
                .post(stellarCommissionRest + "/initsession")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body()
                .as(CommissionInitResponse.class);

        // TODO: asserts about response

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
}
