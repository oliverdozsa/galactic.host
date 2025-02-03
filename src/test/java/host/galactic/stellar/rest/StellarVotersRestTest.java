package host.galactic.stellar.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import host.galactic.testutils.JsonUtils;
import io.quarkus.logging.Log;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.net.URL;

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
                .auth().oauth2(keycloakClient.getAccessToken("bob"))
                .get(location)
                .then()
                .statusCode(403);

        Log.info("[  END TEST]: testGetPrivateVotingAsNonParticipant()\n\n");
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
}
