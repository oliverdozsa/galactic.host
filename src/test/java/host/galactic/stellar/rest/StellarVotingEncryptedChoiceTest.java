package host.galactic.stellar.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.StellarBaseTest;
import host.galactic.stellar.encryption.EncryptedChoice;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import host.galactic.stellar.rest.requests.voting.VotingEncryptChoiceRequest;
import host.galactic.stellar.rest.responses.voting.VotingEncryptChoiceResponse;
import host.galactic.testutils.JsonUtils;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class StellarVotingEncryptedChoiceTest extends StellarBaseTest {
    @Test
    public void testEncryptedOption() {
        Log.info("[START TEST]: testEncryptedOption()");

        var votingId = createAnEncryptedVoting();

        var request = new VotingEncryptChoiceRequest("someChoice");
        var response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.voting.url + "/" + votingId + "/encryptchoice")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body().as(VotingEncryptChoiceResponse.class);

        assertThat(response.encryptedChoice(), not(blankOrNullString()));

        expireEncryptionFor(votingId);

        var voting = rest.voting.getById(votingId, "alice");
        assertThat(voting.decryptionKey(), not(blankOrNullString()));

        var decryptedChoice = EncryptedChoice.decryptFromBase64(response.encryptedChoice(), voting.decryptionKey());
        assertThat(decryptedChoice, equalTo("someChoice"));

        Log.info("[  END TEST]: testEncryptedOption()");
    }

    @Test
    public void testEncryptedOptionButVotingIsNotEncrypted() {
        Log.info("[START TEST]: testEncryptedOptionButVotingIsNotEncrypted()");

        var votingId = createAnUnencryptedVoting();

        var request = new VotingEncryptChoiceRequest("someChoice");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.voting.url + "/" + votingId + "/encryptchoice")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        Log.info("[  END TEST]: testEncryptedOptionButVotingIsNotEncrypted()");
    }

    @Test
    public void testEncryptedOptionVotingDoesNotExist() {
        Log.info("[START TEST]: testEncryptedOptionVotingDoesNotExist()");

        var request = new VotingEncryptChoiceRequest("someChoice");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(rest.voting.url + "/-1/encryptchoice")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        Log.info("[  END TEST]: testEncryptedOptionVotingDoesNotExist()");
    }

    @Test
    public void testGetEncryptionKeyOfVoting() {
        Log.info("[START TEST]: testGetEncryptionKeyOfVoting()");

        var votingId = createAnEncryptedVotingWithEncryptUntilExpired();
        var response = rest.voting.getById(votingId, "alice");

        assertThat(response.decryptionKey(), not(blankOrNullString()));

        Log.info("[  END TEST]: testGetEncryptionKeyOfVoting()");
    }

    @Test
    public void testGetEncryptionKeyOfVotingButEncryptUntilDidNotExpire() {
        Log.info("[START TEST]: testGetEncryptionKeyOfVotingButEncryptUntilDidNotExpire()");

        var votingId = createAnEncryptedVoting();
        var response = rest.voting.getById(votingId, "alice");

        assertThat(response.decryptionKey(), blankOrNullString());

        Log.info("[  END TEST]: testGetEncryptionKeyOfVotingButEncryptUntilDidNotExpire()");
    }

    @Test
    public void testGetEncryptionKeyButVotingDoesNotExist() {
        Log.info("[START TEST]: testGetEncryptionKeyButVotingDoesNotExist()");

        given()
                .contentType(ContentType.JSON)
                .when()
                .get(rest.voting.url + "/-1/encryptionkey")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        Log.info("[  END TEST]: testGetEncryptionKeyButVotingDoesNotExist()");
    }

    private Long createAnEncryptedVoting() {
        return rest.voting.createAs("alice");
    }

    @Transactional
    public Long createAnEncryptedVotingWithEncryptUntilExpired() {
        var votingId = rest.voting.createAs("alice");
        expireEncryptionFor(votingId);
        return votingId;
    }

    private Long createAnUnencryptedVoting() {
        var jsonVotingRequest = JsonUtils.readJsonFile("valid-voting-request.json");
        ((ObjectNode) jsonVotingRequest.get("dates")).remove("encryptedUntil");

        var request = JsonUtils.convertJsonNodeTo(CreateVotingRequest.class, jsonVotingRequest);

        return rest.voting.create(request, "alice");
    }

    @Transactional
    public void expireEncryptionFor(Long votingId) {
        var votingEntity = db.entityManager.find(VotingEntity.class, votingId);
        votingEntity.encryptedUntil = Instant.now().minus(Duration.ofDays(1));
        db.entityManager.persist(votingEntity);
    }
}
