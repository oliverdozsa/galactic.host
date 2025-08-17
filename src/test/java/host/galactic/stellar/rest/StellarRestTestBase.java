package host.galactic.stellar.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import host.galactic.stellar.rest.responses.voting.PageResponse;
import host.galactic.testutils.AuthForTest;
import host.galactic.testutils.JsonUtils;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class StellarRestTestBase {
    @TestHTTPEndpoint(StellarVotingRest.class)
    @TestHTTPResource
    protected URL stellarVotingRest;

    private AuthForTest authForTest = new AuthForTest();

    public List<PageResponse> getPages(String url, String asUser) {
        var pageResponse = getPage(url, asUser,0);
        List<PageResponse> responses = new ArrayList<>();
        responses.add(pageResponse);

        var currentPage = 1;
        while (currentPage < pageResponse.totalPages()) {
            pageResponse = getPage(url, asUser, currentPage);
            responses.add(pageResponse);
            currentPage += 1;
        }

        return responses;
    }

    public List<Long> getIdsFrom(PageResponse response) {
        return response.items().stream()
                .map(this::getIdFromItem)
                .toList();
    }

    public PageResponse getPage(String url, String asUser, int page) {
        String withAccessToken = authForTest.loginAs("alice");
        return given()
                .auth().oauth2(withAccessToken)
                .get(url + "/?page=" + page)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body()
                .as(PageResponse.class);
    }

    public long createAVotingAs(String user) {
        CreateVotingRequest createRequest = makeCreateVotingRequest();
        return createVoting(createRequest, user);
    }

    public CreateVotingRequest makeCreateVotingRequest() {
        ObjectNode votingRequestJson = JsonUtils.readJsonFile("valid-voting-request.json");
        return JsonUtils.convertJsonNodeTo(CreateVotingRequest.class, votingRequestJson);
    }

    public long createVoting(CreateVotingRequest request, String user) {
        String withAccessToken = authForTest.loginAs(user);
        var location = given()
                .auth().oauth2(withAccessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(stellarVotingRest)
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .extract()
                .header("Location");

        String[] locationParts = location.split("/");
        return Long.parseLong(locationParts[locationParts.length - 1]);
    }


    private Long getIdFromItem(Object item) {
        var itemAsMap = (Map) item;
        var id = ((Integer) (itemAsMap.get("id"))).longValue();
        return id;
    }
}
