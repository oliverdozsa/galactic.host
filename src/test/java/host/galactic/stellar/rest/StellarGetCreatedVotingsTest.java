package host.galactic.stellar.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import host.galactic.stellar.rest.responses.voting.PageResponse;
import host.galactic.stellar.rest.responses.voting.VotingResponse;
import host.galactic.testutils.AuthForTest;
import host.galactic.testutils.JsonUtils;
import io.quarkus.logging.Log;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class StellarGetCreatedVotingsTest {
    @TestHTTPEndpoint(StellarVotingRest.class)
    @TestHTTPResource
    private URL stellarVotingRest;

    private AuthForTest authForTest = new AuthForTest();

    @Test
    public void testGetCreatedNotAuthenticated() {
        Log.info("[START TEST]: testGetCreatedNotAuthenticated()");

        given()
                .get(stellarVotingRest + "/created")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());

        Log.info("[  END TEST]: testGetCreatedNotAuthenticated()\n\n");
    }

    @Test
    public void testGetCreatedWithPaging() {
        Log.info("[START TEST]: testGetCreatedWithPaging()");

        createMultipleVotingsForPaging();

        String withAccessToken = authForTest.loginAs("alice");
        given()
                .auth().oauth2(withAccessToken)
                .get(stellarVotingRest + "/created")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("totalPages", greaterThan(0))
                .body("items", hasSize(15));

        Log.info("[  END TEST]: testGetCreatedWithPaging()\n\n");
    }

    @Test
    public void testInvalidPage() {
        Log.info("[START TEST]: testInvalidPage()");

        createMultipleVotingsForPaging();

        String withAccessToken = authForTest.loginAs("alice");
        int totalPages = getTotalPageCount();

        given()
                .auth().oauth2(withAccessToken)
                .get(stellarVotingRest + "/created?page=" + (totalPages + 1))
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        Log.info("[  END TEST]: testInvalidPage()\n\n");
    }

    private void createMultipleVotingsForPaging() {
        for (int i = 0; i < 42; i++) {
            this.createAVotingAsAlice();
        }
    }

    private void createAVotingAsAlice() {
        CreateVotingRequest createRequest = makeCreateVotingRequest();
        String withAccessToken = authForTest.loginAs("alice");
        given()
                .auth().oauth2(withAccessToken)
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post(stellarVotingRest)
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode());
    }

    private CreateVotingRequest makeCreateVotingRequest() {
        ObjectNode votingRequestJson = JsonUtils.readJsonFile("valid-voting-request.json");
        return JsonUtils.convertJsonNodeTo(CreateVotingRequest.class, votingRequestJson);
    }

    private int getTotalPageCount() {
        String withAccessToken = authForTest.loginAs("alice");

        return given()
                .auth().oauth2(withAccessToken)
                .get(stellarVotingRest + "/created")
                .then()
                .extract()
                .body()
                .as(PageResponse.class)
                .totalPages();
    }
}
