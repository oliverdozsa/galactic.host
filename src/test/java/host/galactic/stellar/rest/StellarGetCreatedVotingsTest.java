package host.galactic.stellar.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.stellar.StellarTest;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import host.galactic.stellar.rest.responses.voting.PageResponse;
import host.galactic.testutils.AuthForTest;
import host.galactic.testutils.JsonUtils;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class StellarGetCreatedVotingsTest {
    @Inject
    private AuthForTest auth;

    @Inject
    private StellarTest test;

    @Test
    public void testGetCreatedNotAuthenticated() {
        Log.info("[START TEST]: testGetCreatedNotAuthenticated()");

        given()
                .get(test.getRest().getVoting().getUrl() + "/created")
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

        var votingsCreatedByAliceQueried = test.getRest().getPages(test.getRest().getVoting().getUrl() + "/created", "alice")
                .stream()
                .map(m -> test.getRest().getIdsFrom(m))
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

        var withAccessToken = auth.loginAs("alice");
        int totalPages = getTotalPageCount();

        given()
                .auth().oauth2(withAccessToken)
                .get(test.getRest().getVoting().getUrl() + "/created?page=" + totalPages)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("totalPages", greaterThan(0))
                .body("items", hasSize(0));;

        Log.info("[  END TEST]: testGetCreatedInvalidPage()\n\n");
    }

    @Test
    public void testGetSingleByCreator() {
        var votingId = createPrivateVotingAs("alice");

        var withAccessToken = auth.loginAs("alice");
        given()
                .auth().oauth2(withAccessToken)
                .get(test.getRest().getVoting().getUrl() + "/" + votingId)
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void testGetNotExisting() {
        var asAlice = auth.loginAs("alice");
        given()
                .auth().oauth2(asAlice)
                .get(test.getRest().getVoting().getUrl() + "/-1")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    private List<Long> createMultipleVotingsForPagingAs(String user) {
        var createdVotingIds = new ArrayList<Long>();
        for (int i = 0; i < 42; i++) {
            createdVotingIds.add(test.getRest().getVoting().createAs(user));
        }

        return createdVotingIds;
    }

    private long createPrivateVotingAs(String user) {
        ObjectNode votingRequestJson = JsonUtils.readJsonFile("valid-voting-request.json");
        votingRequestJson.put("visibility", "PRIVATE");

        var createRequest = JsonUtils.convertJsonNodeTo(CreateVotingRequest.class, votingRequestJson);
        return test.getRest().getVoting().create(createRequest, user);
    }

    private int getTotalPageCount() {
        var withAccessToken = auth.loginAs("alice");

        return given()
                .auth().oauth2(withAccessToken)
                .get(test.getRest().getVoting().getUrl() + "/created")
                .then()
                .extract()
                .body()
                .as(PageResponse.class)
                .totalPages();
    }
}
