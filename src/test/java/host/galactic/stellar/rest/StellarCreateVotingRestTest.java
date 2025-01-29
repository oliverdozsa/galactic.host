package host.galactic.stellar.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.stellar.rest.requests.createvoting.CreateVotingRequest;
import host.galactic.stellar.rest.responses.VotingResponse;
import host.galactic.testutils.JsonUtils;
import io.quarkus.logging.Log;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class StellarCreateVotingRestTest {
    @TestHTTPEndpoint(StellarVotingRest.class)
    @TestHTTPResource
    private URL stellarVotingRest;

    @Test
    public void testCreateVoting() {
        Log.info("[START TEST]: testCreateVoting()");

        CreateVotingRequest createRequest = makeCreateVotingRequest();

        String locationHeader = given()
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post(stellarVotingRest)
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        assertThat(locationHeader, not(blankOrNullString()));

        VotingResponse votingResponse = given()
                .get(locationHeader)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(VotingResponse.class);

        assertThat(votingResponse.title(), equalTo(createRequest.title()));

        Log.info("[  END TEST]: testCreateVoting()\n\n");
    }

    @Test
    public void testCreateInvalidVoting() {
        Log.info("[START TEST]: testCreateInvalidVoting()");

        CreateVotingRequest invalidCreateRequest = makeInvalidCreateVotingRequest();

        given()
                .contentType(ContentType.JSON)
                .body(invalidCreateRequest)
                .when()
                .post(stellarVotingRest)
                .then()
                .statusCode(400);

        Log.info("[  END TEST]: testCreateInvalidVoting()\n\n");
    }

    @Test
    public void testNotExistingVoting() {
        Log.info("[START TEST]: testNotExistingVoting()");

        given()
                .when()
                .get(stellarVotingRest + "/42")
                .then()
                .statusCode(404);

        Log.info("[  END TEST]: testNotExistingVoting()\n\n");
    }

    private CreateVotingRequest makeCreateVotingRequest() {
        ObjectNode votingRequestJson = JsonUtils.readJsonFile("valid-voting-request.json");
        return JsonUtils.convertJsonNodeTo(CreateVotingRequest.class, votingRequestJson);
    }

    private CreateVotingRequest makeInvalidCreateVotingRequest() {
        ObjectNode votingRequestJson = JsonUtils.readJsonFile("valid-voting-request.json");
        votingRequestJson.put("title", "a");
        return JsonUtils.convertJsonNodeTo(CreateVotingRequest.class, votingRequestJson);
    }
}
