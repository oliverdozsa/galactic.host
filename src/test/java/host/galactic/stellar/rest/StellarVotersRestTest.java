package host.galactic.stellar.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.stellar.rest.requests.voting.AddVotersRequest;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import host.galactic.testutils.JsonUtils;
import io.quarkus.logging.Log;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.List;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class StellarVotersRestTest {
    @TestHTTPEndpoint(StellarVotingRest.class)
    @TestHTTPResource
    private URL stellarVotingRest;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @Test
    public void testGetPrivateVotingAsNonParticipant() {
        Log.info("[START TEST]: testGetPrivateVotingAsNonParticipant()");
        String location = createPrivateVotingByAlice();

        given()
                .auth().oauth2(keycloakClient.getAccessToken("charlie"))
                .get(location)
                .then()
                .statusCode(403);

        Log.info("[  END TEST]: testGetPrivateVotingAsNonParticipant()\n\n");
    }

    @Test
    public void testGetVotingWithEmailNotVerifiedClaim() {
        Log.info("[START TEST]: testGetVotingWithEmailNotVerifiedClaim()");

        String location = createUnlistedVotingByAlice();

        given()
                .auth().oauth2(keycloakClient.getAccessToken("george"))
                .get(location)
                .then()
                .statusCode(403);

        Log.info("[  END TEST]: testGetVotingWithEmailNotVerifiedClaim()\n\n");
    }

    @Test
    public void testGetVotingWithEmailNotPresentClaim() {
        Log.info("[START TEST]: testGetVotingWithEmailNotPresentClaim()");

        String location = createUnlistedVotingByAlice();

        given()
                .auth().oauth2(keycloakClient.getAccessToken("helena"))
                .get(location)
                .then()
                .statusCode(403);

        Log.info("[  END TEST]: testGetVotingWithEmailNotPresentClaim()\n\n");
    }

    @Test
    public void testGetPrivateVotingByParticipant() {
        Log.info("[START TEST]: testGetPrivateVotingByParticipant()");

        String location = createPrivateVotingByAlice();
        String[] locationParts = location.split("/");
        Long id = Long.parseLong(locationParts[locationParts.length - 1]);

        AddVotersRequest addVotersRequest = new AddVotersRequest(List.of("emily@galactic.pub", "duke@galactic.pub", "alice@galactic.pub"));
        given()
                .auth().oauth2(keycloakClient.getAccessToken("alice"))
                .contentType(ContentType.JSON)
                .body(addVotersRequest)
                .post(stellarVotingRest + "/addvoters/" + id)
                .then()
                .statusCode(204);

        given()
                .auth().oauth2(keycloakClient.getAccessToken("emily"))
                .get(location)
                .then()
                .statusCode(200);

        Log.info("[  END TEST]: testGetPrivateVotingByParticipant()\n\n");
    }

    private String createPrivateVotingByAlice() {
        ObjectNode createRequest = JsonUtils.readJsonFile("valid-voting-request.json");
        createRequest.put("visibility", CreateVotingRequest.Visibility.PRIVATE.name());

        return given()
                .auth().oauth2(keycloakClient.getAccessToken("alice"))
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post(stellarVotingRest)
                .then()
                .statusCode(201)
                .extract()
                .header("Location");
    }

    private String createUnlistedVotingByAlice() {
        ObjectNode createRequest = JsonUtils.readJsonFile("valid-voting-request.json");
        createRequest.put("visibility", CreateVotingRequest.Visibility.UNLISTED.name());

        return given()
                .auth().oauth2(keycloakClient.getAccessToken("alice"))
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post(stellarVotingRest)
                .then()
                .statusCode(201)
                .extract()
                .header("Location");
    }
}
