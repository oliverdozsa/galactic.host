package host.galactic.testutils;

import host.galactic.stellar.rest.responses.voting.PageResponse;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class TestRestUtils {
    private static AuthForTest authForTest = new AuthForTest();

    public static List<PageResponse> getPages(String url, String asUser) {
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

    public static List<Long> getIdsFrom(PageResponse response) {
        return response.items().stream()
                .map(TestRestUtils::getIdFromItem)
                .toList();
    }

    public static PageResponse getPage(String url, String asUser, int page) {
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

    private static Long getIdFromItem(Object item) {
        var itemAsMap = (Map) item;
        var id = ((Integer) (itemAsMap.get("id"))).longValue();
        return id;
    }
}
