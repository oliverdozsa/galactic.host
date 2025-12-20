package host.galactic.stellar;

import host.galactic.stellar.rest.responses.voting.PageResponse;
import host.galactic.testutils.AuthForTest;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class StellarBaseTestRest {
    private AuthForTest authForTest;

    public StellarBaseTestRestVoting voting;
    public StellarBaseTestRestCommission commission;

    public StellarBaseTestRest(AuthForTest authForTest, StellarBaseTestRestVoting voting, StellarBaseTestRestCommission commission) {
        this.authForTest = authForTest;
        this.voting = voting;
        this.commission = commission;
    }

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
        var withAccessToken = authForTest.loginAs(asUser);
        return given()
                .auth().oauth2(withAccessToken)
                .get(url + "/?page=" + page)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body()
                .as(PageResponse.class);
    }

    private Long getIdFromItem(Object item) {
        var itemAsMap = (Map) item;
        var id = ((Integer) (itemAsMap.get("id"))).longValue();
        return id;
    }
}
