package host.galactic.stellar;

import host.galactic.stellar.rest.responses.voting.PageResponse;
import host.galactic.testutils.AuthForTest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

@ApplicationScoped
public class StellarRestTestBase {
    @Inject
    private AuthForTest authForTest;

    @Inject
    private StellarRestVotingTestBase voting;

    @Inject
    private StellarRestCommissionTestBase commission;

    public StellarRestVotingTestBase getVoting() {
        return voting;
    }

    public StellarRestCommissionTestBase getCommission() {
        return commission;
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
