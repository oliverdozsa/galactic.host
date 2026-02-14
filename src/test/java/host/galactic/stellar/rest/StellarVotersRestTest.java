package host.galactic.stellar.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;

import host.galactic.stellar.StellarBaseTest;
import host.galactic.stellar.rest.requests.voting.AddVotersRequest;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import host.galactic.stellar.rest.responses.voting.VotingResponse;

import host.galactic.testutils.AuthForTest;
import host.galactic.testutils.JsonUtils;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class StellarVotersRestTest extends StellarBaseTest {
    @Inject
    AuthForTest auth;

    @Test
    public void testGetPrivateVotingAsNonParticipant() {
        Log.info("[START TEST]: testGetPrivateVotingAsNonParticipant()");
        var location = createPrivateVotingByAlice();

        var withAccessToken = auth.loginAs("charlie");
        given()
                .auth().oauth2(withAccessToken)
                .get(location)
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

        Log.info("[  END TEST]: testGetPrivateVotingAsNonParticipant()\n\n");
    }

    @Test
    public void testGetVotingUnauthenticated() {
        Log.info("[START TEST]: testGetVotingUnauthenticated()");
        var location = createPrivateVotingByAlice();

        given()
                .get(location)
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());

        Log.info("[  END TEST]: testGetVotingUnauthenticated()\n\n");
    }

    @Test
    public void testGetVotingWithEmailNotPresent() {
        Log.info("[START TEST]: testGetVotingWithEmailNotPresent()");

        var location = createUnlistedVotingByAlice();

        var withAccessToken = auth.loginAs("helena");
        given()
                .auth().oauth2(withAccessToken)
                .get(location)
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

        Log.info("[  END TEST]: testGetVotingWithEmailNotPresent()\n\n");
    }

    @Test
    public void testGetPrivateVotingByParticipant() {
        Log.info("[START TEST]: testGetPrivateVotingByParticipant()");

        var location = createPrivateVotingByAlice();
        String[] locationParts = location.split("/");
        Long id = Long.parseLong(locationParts[locationParts.length - 1]);

        AddVotersRequest addVotersRequest = new AddVotersRequest(List.of("emily@galactic.pub", "duke@galactic.pub", "alice@galactic.pub"));
        var withAccessTokenForAlice = auth.loginAs("alice");
        given()
                .auth().oauth2(withAccessTokenForAlice)
                .contentType(ContentType.JSON)
                .body(addVotersRequest)
                .post(rest.voting.url + "/addvoters/" + id)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        var withAccessTokenForEmily = auth.loginAs("emily");
        var response = given()
                .auth().oauth2(withAccessTokenForEmily)
                .get(location)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body()
                .as(VotingResponse.class);

        assertThat(response.isParticipant(), is(true));

        Log.info("[  END TEST]: testGetPrivateVotingByParticipant()\n\n");
    }

    @Test
    public void testMaxVotersExceeded() {
        Log.info("[START TEST]: testMaxVotersExceeded()");

        var location = createPrivateVotingByAlice();
        String[] locationParts = location.split("/");
        Long id = Long.parseLong(locationParts[locationParts.length - 1]);

        var addVotersRequest = new AddVotersRequest(List.of(
                "emily@galactic.pub",
                "duke@galactic.pub",
                "alice@galactic.pub",
                "charlie@galactic.pub",
                "frank@galactic.pub",
                "bob@galactic.pub"));

        var withAccessToken = auth.loginAs("alice");
        given()
                .auth().oauth2(withAccessToken)
                .contentType(ContentType.JSON)
                .body(addVotersRequest)
                .post(rest.voting.url + "/addvoters/" + id)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        Log.info("[  END TEST]: testMaxVotersExceeded()\n\n");
    }

    @Test
    public void testDuplicateEmailsInAddVotersRequest() {
        Log.info("[START TEST]: testDuplicateEmailsInAddVotersRequest()\n\n");

        var location = createPrivateVotingByAlice();
        String[] locationParts = location.split("/");
        Long id = Long.parseLong(locationParts[locationParts.length - 1]);

        var addVotersRequest = new AddVotersRequest(List.of(
                "emily@galactic.pub",
                "emily@galactic.pub",
                "alice@galactic.pub",
                "charlie@galactic.pub"));

        var withAccessToken = auth.loginAs("alice");
        given()
                .auth().oauth2(withAccessToken)
                .contentType(ContentType.JSON)
                .body(addVotersRequest)
                .post(rest.voting.url + "/addvoters/" + id)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        var votingResponse = given()
                .auth().oauth2(withAccessToken)
                .get(location)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(VotingResponse.class);

        assertThat(votingResponse.numOfVoters(), equalTo(3));

        Log.info("[  END TEST]: testDuplicateEmailsInAddVotersRequest()\n\n");
    }

    @Test
    public void testAddVotersNotByVotingOwner() {
        Log.info("[START TEST]: testAddVotersNotByVotingOwner()\n\n");

        var location = createPrivateVotingByAlice();
        String[] locationParts = location.split("/");
        Long id = Long.parseLong(locationParts[locationParts.length - 1]);

        var addVotersRequest = new AddVotersRequest(List.of(
                "emily@galactic.pub",
                "emily@galactic.pub",
                "alice@galactic.pub",
                "charlie@galactic.pub"));

        var withAccessToken = auth.loginAs("charlie");
        given()
                .auth().oauth2(withAccessToken)
                .contentType(ContentType.JSON)
                .body(addVotersRequest)
                .post(rest.voting.url + "/addvoters/" + id)
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

        Log.info("[  END TEST]: testAddVotersNotByVotingOwner()\n\n");
    }

    private String createPrivateVotingByAlice() {
        var createRequest = JsonUtils.readJsonFile("valid-voting-request.json");
        createRequest.put("visibility", CreateVotingRequest.Visibility.PRIVATE.name());

        var withAccessToken = auth.loginAs("alice");
        return given()
                .auth().oauth2(withAccessToken)
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post(rest.voting.url)
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .extract()
                .header("Location");
    }

    private String createUnlistedVotingByAlice() {
        ObjectNode createRequest = JsonUtils.readJsonFile("valid-voting-request.json");
        createRequest.put("visibility", CreateVotingRequest.Visibility.UNLISTED.name());

        var withAccessToken = auth.loginAs("alice");
        return given()
                .auth().oauth2(withAccessToken)
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post(rest.voting.url)
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .extract()
                .header("Location");
    }
}
