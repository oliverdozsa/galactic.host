package host.galactic.stellar.rest;

import host.galactic.data.entities.VotingEntity;
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

import java.time.Duration;
import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class StellarCommissionRestSignEnvelopeTest extends StellarBaseTest {
    @Inject
    AuthForTest auth;

    @BeforeEach
    @Transactional
    public void deleteAllVotings() {
        db.deleteAllVoting();
    }

    @Test
    public void testSignEnvelope() {
        Log.info("[START TEST]: testSignEnvelope()");

        var votingId = createAnAlreadyStartedVoting("alice", new String[]{"charlie", "bob"});

        var base64Envelope = utils.toBase64("someMessage");
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

        var votingId = createAnAlreadyStartedVoting("alice", new String[]{"charlie", "bob"});
        var base64Envelope = utils.toBase64("someMessage");
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

        var votingId = createAnAlreadyStartedVoting("alice", new String[]{"charlie", "bob"});

        var base64Envelope = utils.toBase64("someMessage");
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
        var base64Envelope = utils.toBase64("someMessage");
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

        var votingId = createAnAlreadyStartedVoting("alice", new String[]{"charlie", "bob"});

        var base64Envelope = utils.toBase64("someMessage");
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


        base64Envelope = utils.toBase64("someOtherMessage");
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

        var votingId = createAnAlreadyStartedVoting("alice", new String[]{"charlie", "bob"});

        var base64Envelope = utils.toBase64("someMessage");
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
                .when()
                .get(rest.commission.url + "/signature/?voting=" + votingId)
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
                .when()
                .get(rest.commission.url + "/signature/?voting=-1")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        Log.info("[  END TEST]: testGetNonExistingSignature()");
    }

    @Test
    public void testSigningAnEnvelopeButVotingIsNotStarted() {
        Log.info("[START TEST]: testSigningAnEnvelopeButVotingIsNotStarted()");

        var votingId = rest.voting.createWithParticipants("alice", new String[]{"charlie", "bob"});

        var base64Envelope = utils.toBase64("someMessage");
        var request = new CommissionSignEnvelopeRequest(base64Envelope);
        var asBob = auth.loginAs("bob");
        given()
                .auth().oauth2(asBob)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/signenvelope/" + votingId)
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

        Log.info("[  END TEST]: testSigningAnEnvelopeButVotingIsNotStarted()");
    }

    @Test
    public void testSigningAnEnvelopeButVotingEnded() {
        Log.info("[START TEST]: testSigningAnEnvelopeButVotingEnded()");

        var votingId = rest.voting.createWithParticipants("alice", new String[]{"charlie", "bob"});
        setEndDateBeforeTodayFor(votingId);

        var base64Envelope = utils.toBase64("someMessage");
        var request = new CommissionSignEnvelopeRequest(base64Envelope);
        var asBob = auth.loginAs("bob");
        given()
                .auth().oauth2(asBob)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/signenvelope/" + votingId)
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

        Log.info("[  END TEST]: testSigningAnEnvelopeButVotingEnded()");
    }

    private Long createAnAlreadyStartedVoting(String owner, String[] participants) {
        var votingId = rest.voting.createWithParticipants(owner, participants);
        setStartDayBeforeTodayFor(votingId);
        return votingId;
    }

    @Transactional
    public void setStartDayBeforeTodayFor(Long votingId) {
        var voting = db.entityManager.find(VotingEntity.class, votingId);
        voting.startDate = Instant.now().minus(Duration.ofDays(2));
        db.entityManager.persist(voting);
    }

    @Transactional
    public void setEndDateBeforeTodayFor(Long votingId) {
        var voting = db.entityManager.find(VotingEntity.class, votingId);
        voting.endDate = Instant.now().minus(Duration.ofDays(1));
        db.entityManager.persist(voting);
    }
}
