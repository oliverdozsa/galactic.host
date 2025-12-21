package host.galactic.stellar.rest;

import host.galactic.stellar.StellarBaseTest;
import host.galactic.stellar.rest.responses.commission.CommissionGetPublicKeyResponse;
import host.galactic.testutils.AuthForTest;
import io.quarkus.logging.Log;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class StellarCommissionPublicKeyTest extends StellarBaseTest {
    @Inject
    private AuthForTest auth;

    @BeforeEach
    @Transactional
    public void deleteAllVotings() {
        db.deleteAllVoting();
    }

    @Test
    @TestTransaction
    public void testPublicKey() {
        Log.info("[START TEST]: testPublicKey()");

        var response = given()
                .contentType(ContentType.JSON)
                .when()
                .post(rest.commission.url + "/initsession")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body()
                .as(CommissionGetPublicKeyResponse.class);

        assertThat(response.publicKey(), notNullValue());

        Log.info("[  END TEST]: testPublicKey()");
    }

    // TODO: should be used elsewhere
    public Long initializeAVotingWithParticipant(String voter) {
        var votingId = rest.voting.createAs("alice");
        rest.voting.addVoterAsParticipantTo(votingId, "charlie", "alice");

        waitForAssetAccountsToBeCreatedFor(votingId);
        waitForChannelAccountsToBeCreatedFor(votingId);

        return votingId;
    }

    public void waitForChannelAccountsToBeCreatedFor(Long votingId) {
        await().until(() -> areChannelAccountsCreatedFor(votingId));
    }

    public void waitForAssetAccountsToBeCreatedFor(Long votingId) {
        await().until(() -> areAssetAccountsCreatedFor(votingId));
    }

    @Transactional
    public boolean areAssetAccountsCreatedFor(Long votingId) {
        return db.areAssetAccountsCreatedFor(votingId);
    }

    @Transactional
    public boolean areChannelAccountsCreatedFor(Long votingId) {
        return db.areChannelAccountsCreatedFor(votingId);
    }
}
