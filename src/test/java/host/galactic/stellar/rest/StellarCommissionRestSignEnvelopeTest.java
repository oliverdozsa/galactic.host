package host.galactic.stellar.rest;

import host.galactic.stellar.StellarBaseTest;
import host.galactic.stellar.rest.requests.commission.CommissionSignEnvelopeRequest;
import host.galactic.stellar.rest.responses.commission.CommissionSignEnvelopeResponse;
import host.galactic.testutils.AuthForTest;
import io.quarkus.logging.Log;
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
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;

@QuarkusTest
public class StellarCommissionRestSignEnvelopeTest extends StellarBaseTest {
    @Inject
    private AuthForTest auth;

    @BeforeEach
    @Transactional
    public void deleteAllVotings() {
        db.deleteAllVoting();
    }

    @Test
    public void testSignEnvelope() {
        Log.info("[START TEST]: testSignEnvelope()");

        var votingId = rest.voting.createWithParticipants("alice", new String[]{"charlie", "bob"});

        var base64Envelope = utils.createEnvelopeFor("someMessage");
        var request = new CommissionSignEnvelopeRequest(base64Envelope);
        var asBob = auth.loginAs("bob");
        var response = given()
                .auth().oauth2(asBob)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/signenvelope/" + votingId)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body()
                .as(CommissionSignEnvelopeResponse.class);

        assertThat(response.signature(), not(blankOrNullString()));

        Log.info("[  END TEST]: testSignEnvelope");
    }

    @Test
    public void testSignEnvelopeUnauthenticated() {
        Log.info("[START TEST]: testSignEnvelopeUnauthenticated()");

        var votingId = rest.voting.createWithParticipants("alice", new String[]{"charlie", "bob"});
        var base64Envelope = utils.createEnvelopeFor("someMessage");
        var request = new CommissionSignEnvelopeRequest(base64Envelope);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/signenvelope/" + votingId)
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());

        Log.info("[  END TEST]: testSignEnvelopeUnauthenticated()");
    }

    @Test
    public void testSignEnvelopeUserIsNotParticipant() {
        Log.info("[START TEST]: testSignEnvelopeUserIsNotParticipant()");

        var votingId = rest.voting.createWithParticipants("alice", new String[]{"charlie", "bob"});

        var base64Envelope = utils.createEnvelopeFor("someMessage");
        var request = new CommissionSignEnvelopeRequest(base64Envelope);
        var asDuke = auth.loginAs("duke");
        given()
                .auth().oauth2(asDuke)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/signenvelope/" + votingId)
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

        Log.info("[  END TEST]: testSignEnvelopeUserIsNotParticipant()");
    }

    @Test
    public void testSignEnvelopeInvalidVoting() {
        Log.info("[START TEST]: testSignEnvelopeInvalidVoting()");

        var nonExistingVotingId = -1L;
        var base64Envelope = utils.createEnvelopeFor("someMessage");
        var request = new CommissionSignEnvelopeRequest(base64Envelope);
        var asBob = auth.loginAs("bob");
        given()
                .auth().oauth2(asBob)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/signenvelope/" + nonExistingVotingId)
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        Log.info("[  END TEST]: testSignEnvelopeInvalidVoting()");
    }

    @Test
    public void testDoubleSigningEnvelope() {
        Log.info("[START TEST]: testDoubleSigningEnvelope()");

        var votingId = rest.voting.createWithParticipants("alice", new String[]{"charlie", "bob"});

        var base64Envelope = utils.createEnvelopeFor("someMessage");
        var request = new CommissionSignEnvelopeRequest(base64Envelope);
        var asBob = auth.loginAs("bob");
        var response = given()
                .auth().oauth2(asBob)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/signenvelope/" + votingId)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body()
                .as(CommissionSignEnvelopeResponse.class);

        assertThat(response.signature(), not(blankOrNullString()));


        base64Envelope = utils.createEnvelopeFor("someOtherMessage");
        request = new CommissionSignEnvelopeRequest(base64Envelope);
        given()
                .auth().oauth2(asBob)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/signenvelope/" + votingId)
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

        Log.info("[  END TEST]: testDoubleSigningEnvelope()");
    }

    @Test
    public void testGetSignature() {
        Log.info("[START TEST]: testGetSignature()");

        var votingId = rest.voting.createWithParticipants("alice", new String[]{"charlie", "bob"});

        var base64Envelope = utils.createEnvelopeFor("someMessage");
        var request = new CommissionSignEnvelopeRequest(base64Envelope);
        var asBob = auth.loginAs("bob");

        given()
                .auth().oauth2(asBob)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/signenvelope/" + votingId)
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        var response = given()
                .auth().oauth2(asBob)
                .contentType(ContentType.JSON)
                .when()
                .get(rest.commission.url + "/signature/voting=" + votingId)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body()
                .as(CommissionSignEnvelopeResponse.class);

        assertThat(response.signature(), not(blankOrNullString()));

        Log.info("[  END TEST]: testGetSignature()");
    }

    @Test
    public void testGetNonExistingSignature() {
        Log.info("[START TEST]: testGetNonExistingSignature()");

        var asBob = auth.loginAs("bob");
        given()
                .auth().oauth2(asBob)
                .contentType(ContentType.JSON)
                .when()
                .get(rest.commission.url + "/signature/voting=-1")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .extract().body()
                .as(CommissionSignEnvelopeResponse.class);

        Log.info("[  END TEST]: testGetNonExistingSignature()");
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
