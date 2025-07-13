package host.galactic.stellar.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import host.galactic.stellar.rest.responses.voting.PageResponse;
import host.galactic.testutils.AuthForTest;
import host.galactic.testutils.JsonUtils;
import host.galactic.testutils.TestRestUtils;
import io.quarkus.logging.Log;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static host.galactic.testutils.TestRestUtils.getIdsFrom;
import static host.galactic.testutils.TestRestUtils.getPages;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
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

        var votingsCreatedByAlice = createMultipleVotingsForPagingAs("alice")
                .toArray(new Long[]{});
        var votingsCreatedByCharlie = createMultipleVotingsForPagingAs("charlie")
                .toArray(new Long[]{});

        var votingsCreatedByAliceQueried = getPages(stellarVotingRest + "/created", "alice")
                .stream()
                .map(TestRestUtils::getIdsFrom)
                .flatMap(Collection::stream)
                .toList();

        assertThat(votingsCreatedByAliceQueried, hasItems(votingsCreatedByAlice));
        assertThat(votingsCreatedByAliceQueried, not(hasItems(votingsCreatedByCharlie)));

        Log.info("[  END TEST]: testGetCreatedWithPaging()\n\n");
    }

    @Test
    public void testGetCreatedInvalidPage() {
        Log.info("[START TEST]: testGetCreatedInvalidPage()");

        createMultipleVotingsForPagingAs("alice");

        String withAccessToken = authForTest.loginAs("alice");
        int totalPages = getTotalPageCount();

        given()
                .auth().oauth2(withAccessToken)
                .get(stellarVotingRest + "/created?page=" + totalPages)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("totalPages", greaterThan(0))
                .body("items", hasSize(0));;

        Log.info("[  END TEST]: testGetCreatedInvalidPage()\n\n");
    }

    private List<Long> createMultipleVotingsForPagingAs(String user) {
        var createdVotingIds = new ArrayList<Long>();
        for (int i = 0; i < 42; i++) {
            createdVotingIds.add(this.createAVotingAs(user));
        }

        return createdVotingIds;
    }

    private long createAVotingAs(String user) {
        CreateVotingRequest createRequest = makeCreateVotingRequest();
        String withAccessToken = authForTest.loginAs(user);
        var location = given()
                .auth().oauth2(withAccessToken)
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post(stellarVotingRest)
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .extract()
                .header("Location");

        String[] locationParts = location.split("/");
        return Long.parseLong(locationParts[locationParts.length - 1]);
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
