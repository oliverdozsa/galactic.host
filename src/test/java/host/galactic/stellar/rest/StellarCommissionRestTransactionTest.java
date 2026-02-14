package host.galactic.stellar.rest;

import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.StellarBaseTest;
import host.galactic.stellar.rest.requests.commission.CommissionCreateTransactionRequest;

import host.galactic.stellar.rest.requests.commission.CommissionGetTransactionOfSignatureRequest;
import host.galactic.stellar.rest.responses.commission.CommissionCreateTransactionResponse;
import host.galactic.stellar.rest.responses.commission.CommissionGetTransactionOfSignatureResponse;
import host.galactic.testutils.RsaEnvelope;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
public class StellarCommissionRestTransactionTest extends StellarBaseTest {
    private static final String TEST_STELLAR_ACCOUNT = "GBM3C4UKUCG3COTA3G4FXAG5427CXU4DADODFTGNEEJJC55WOEODU7KG";

    @BeforeEach
    @Transactional
    public void deleteAllVotings() {
        db.deleteAllVoting();
    }

    @Test
    public void testCreateTransaction() {
        Log.info("[START TEST]: testCreateTransaction()");

        var votingId = createAnAlreadyStartedVoting("alice", new String[]{"bob", "charlie"});

        waitForAssetAccountsToBeCreatedFor(votingId);
        waitForChannelAccountsToBeCreatedFor(votingId);

        var message = votingId + "|" + TEST_STELLAR_ACCOUNT;
        var revealedSignatureBase64 = createRevealedSignatureFor(message, votingId, "bob");

        var request = new CommissionCreateTransactionRequest(message, revealedSignatureBase64);
        var response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/transaction/")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body()
                .as(CommissionCreateTransactionResponse.class);

        assertThat(response.transaction(), not(blankOrNullString()));

        Log.info("[  END TEST]: testCreateTransaction()");
    }

    @Test
    public void testCreateTransactionButVotingIsNotPreparedYet() {
        Log.info("[START TEST]: testCreateTransactionButVotingIsNotPreparedYet()");
        var votingId = createAnAlreadyStartedVoting("alice", new String[]{"bob", "charlie"});

        var message = votingId + "|" + TEST_STELLAR_ACCOUNT;
        var revealedSignatureBase64 = createRevealedSignatureFor(message, votingId, "bob");

        var request = new CommissionCreateTransactionRequest(message, revealedSignatureBase64);
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/transaction/")
                .then()
                .statusCode(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());

        Log.info("[  END TEST]: testCreateTransactionButVotingIsNotPreparedYet()");
    }

    @Test
    public void testCreateTransactionInvalidVotingId() {
        Log.info("[START TEST]: testCreateTransactionInvalidVotingId()");

        var votingId = createAnAlreadyStartedVoting("alice", new String[]{"bob", "charlie"});

        waitForAssetAccountsToBeCreatedFor(votingId);
        waitForChannelAccountsToBeCreatedFor(votingId);

        var message = "-1|" + TEST_STELLAR_ACCOUNT;
        var revealedSignatureBase64 = createRevealedSignatureFor(message, votingId, "bob");

        var request = new CommissionCreateTransactionRequest(message, revealedSignatureBase64);
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/transaction/")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        Log.info("[  END TEST]: testCreateTransactionInvalidVotingId()");
    }

    @Test
    public void testCreateTransactionButAlreadySentIt() {
        Log.info("[START TEST]: testCreateTransactionButAlreadySentIt()");

        var votingId = createAnAlreadyStartedVoting("alice", new String[]{"bob", "charlie"});

        waitForAssetAccountsToBeCreatedFor(votingId);
        waitForChannelAccountsToBeCreatedFor(votingId);

        var message = votingId + "|" + TEST_STELLAR_ACCOUNT;
        var revealedSignatureBase64 = createRevealedSignatureFor(message, votingId, "bob");

        var request = new CommissionCreateTransactionRequest(message, revealedSignatureBase64);
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/transaction/")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/transaction/")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        Log.info("[  END TEST]: testCreateTransactionButAlreadySentIt()");
    }

    @Test
    public void testGetTransactionForSignature() {
        Log.info("[START TEST]: testGetTransactionForSignature()");

        var votingId = createAnAlreadyStartedVoting("alice", new String[]{"bob", "charlie"});

        waitForAssetAccountsToBeCreatedFor(votingId);
        waitForChannelAccountsToBeCreatedFor(votingId);

        var message = votingId + "|" + TEST_STELLAR_ACCOUNT;
        var revealedSignatureBase64 = createRevealedSignatureFor(message, votingId, "bob");

        var request = new CommissionCreateTransactionRequest(message, revealedSignatureBase64);
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/transaction/")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        var getTxOfSignatureRequest = new CommissionGetTransactionOfSignatureRequest(revealedSignatureBase64);
        var getTxOfSignatureResponse = given()
                .contentType(ContentType.JSON)
                .body(getTxOfSignatureRequest)
                .when()
                .post(rest.commission.url + "/transactionofsignature/")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body().as(CommissionGetTransactionOfSignatureResponse.class);

        assertThat(getTxOfSignatureResponse.transaction(), not(blankOrNullString()));

        Log.info("[  END TEST]: testGetTransactionForSignature()");
    }

    @Test
    public void testTryGettingNonExistingTransactionForSignature() {
        Log.info("[START TEST]: testTryGettingNonExistingTransactionForSignature()");

        var votingId = createAnAlreadyStartedVoting("alice", new String[]{"bob", "charlie"});

        waitForAssetAccountsToBeCreatedFor(votingId);
        waitForChannelAccountsToBeCreatedFor(votingId);

        var message = votingId + "|" + TEST_STELLAR_ACCOUNT;
        var revealedSignatureBase64 = createRevealedSignatureFor(message, votingId, "bob");

        var request = new CommissionCreateTransactionRequest(message, revealedSignatureBase64);
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/transaction/")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        var getTxOfSignatureRequest = new CommissionGetTransactionOfSignatureRequest("someRandomSig");
        given()
                .contentType(ContentType.JSON)
                .body(getTxOfSignatureRequest)
                .when()
                .post(rest.commission.url + "/transactionofsignature/")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        Log.info("[  END TEST]: testTryGettingNonExistingTransactionForSignature()");
    }

    @Test
    public void testCreateTransactionButSignatureIsInvalid() {
        Log.info("[START TEST]: testCreateTransactionButSignatureIsInvalid()");

        var votingId = createAnAlreadyStartedVoting("alice", new String[]{"bob", "charlie"});

        waitForAssetAccountsToBeCreatedFor(votingId);
        waitForChannelAccountsToBeCreatedFor(votingId);

        var message = votingId + "|" + TEST_STELLAR_ACCOUNT;

        var request = new CommissionCreateTransactionRequest(message, "someRandomSignature");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.commission.url + "/transaction/")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        Log.info("[  END TEST]: testCreateTransactionButSignatureIsInvalid()");
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

    private String createRevealedSignatureFor(String message, Long votingId, String user) {
        var signingKeyPublic = rest.commission.getPublicKey().publicKey();

        var rsaEnvelope = new RsaEnvelope(signingKeyPublic);
        var envelopedMessage = rsaEnvelope.create(message.getBytes());
        var base64Envelope = utils.toBase64(envelopedMessage);

        var envelopeSignatureResponse = rest.commission.signEnvelope(base64Envelope, user, votingId);
        var envelopeSignatureBytes = utils.fromBase64(envelopeSignatureResponse.signature());
        var revealedSignature = rsaEnvelope.revealedSignature(envelopeSignatureBytes);
        return utils.toBase64(revealedSignature);
    }

    @Transactional
    public Long createAnAlreadyStartedVoting(String owner, String[] participants) {
        var votingId = rest.voting.createWithParticipants(owner, participants);

        var voting = db.entityManager.find(VotingEntity.class, votingId);
        voting.startDate = Instant.now().minus(Duration.ofDays(2));
        db.entityManager.persist(voting);

        return votingId;
    }
}
