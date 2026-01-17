package host.galactic.stellar.rest;

import host.galactic.stellar.StellarBaseTest;
import host.galactic.stellar.rest.requests.commission.CommissionCreateTransactionRequest;

import host.galactic.stellar.rest.responses.commission.CommissionCreateTransactionResponse;
import host.galactic.testutils.RsaEnvelope;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        var votingId = rest.voting.createWithParticipants("alice", new String[]{"bob", "charlie"});

        waitForAssetAccountsToBeCreatedFor(votingId);
        waitForChannelAccountsToBeCreatedFor(votingId);

        var message = votingId + "|" + TEST_STELLAR_ACCOUNT;
        var signingKeyPublic = rest.commission.getPublicKey().publicKey();

        var rsaEnvelope = new RsaEnvelope(signingKeyPublic);
        var envelopedMessage = rsaEnvelope.create(message.getBytes());
        var base64Envelope = utils.toBase64(envelopedMessage);

        var envelopeSignatureResponse = rest.commission.signEnvelope(base64Envelope);
        var envelopeSignatureBytes = utils.fromBase64(envelopeSignatureResponse.signature());
        var revealedSignature = rsaEnvelope.revealedSignature(envelopeSignatureBytes);
        var revealedSignatureBase64 = utils.toBase64(revealedSignature);

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
    }

    @Test
    public void testCreateTransactionButNotEnoughChannelAccounts() {
        fail("Implement testCreateTransactionButNotEnoughChannelAccounts()");
    }

    @Test
    public void testCreateTransactionButAssetAccountsAreNotYetCreated() {
        fail("Implement testCreateTransactionButAssetAccountsAreNotYetCreated()");
    }

    @Test
    public void testCreateTransactionInvalidVotingId() {
        fail("Implement testCreateTransactionInvalidVotingId()");
    }

    @Test
    public void testCreateTransactionButAlreadySentIt() {
        fail("Implement testCreateTransactionButAlreadySentIt()");
    }

    @Test
    public void testGetTransactionForSignature() {
        fail("Implement testGetTransactionForSignature()");
    }

    @Test
    public void testTryGettingNonExistingTransactionForSignature() {
        fail("Implement testTryGettingNonExistingTransactionForSignature()");
    }

    @Test
    public void testCreateTransactionButSignatureIsInvalid() {
        fail("Implement testCreateTransactionButSignatureIsInvalid()");
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

    private String createSignatureFor(Long votingId)
}
