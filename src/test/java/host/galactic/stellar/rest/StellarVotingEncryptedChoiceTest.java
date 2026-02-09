package host.galactic.stellar.rest;

import host.galactic.stellar.StellarBaseTest;
import host.galactic.stellar.rest.requests.voting.VotingEncryptChoiceRequest;
import host.galactic.stellar.rest.responses.voting.VotingEncryptChoiceResponse;
import host.galactic.stellar.rest.responses.voting.VotingEncryptionKeyResponse;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;

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

        var response = given()
                .contentType(ContentType.JSON)
                .when()
                .get(rest.voting.url + "/" + votingId + "/encryptionkey")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body().as(VotingEncryptionKeyResponse.class);

        assertThat(response.key(), not(blankOrNullString()));

        Log.info("[  END TEST]: testGetEncryptionKeyOfVoting()");
    }

    @Test
    public void testGetEncryptionKeyOfVotingButEncryptUntilDidNotExpire() {
        Log.info("[START TEST]: testGetEncryptionKeyOfVotingButEncryptUntilDidNotExpire()");

        var votingId = createAnEncryptedVotingWithEncryptUntilExpired();

        given()
                .contentType(ContentType.JSON)
                .when()
                .get(rest.voting.url + "/" + votingId + "/encryptionkey")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

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
        // TODO
        return 0L;
    }

    private Long createAnEncryptedVotingWithEncryptUntilExpired() {
        // TODO
        return 0L;
    }

    private Long createAnUnencryptedVoting() {
        // TODO
        return 0L;
    }


}
