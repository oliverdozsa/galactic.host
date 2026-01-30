package host.galactic.stellar.rest;

import host.galactic.stellar.StellarBaseTest;
import host.galactic.stellar.rest.requests.voting.AddVotersRequest;
import host.galactic.stellar.rest.responses.voting.PageResponse;
import host.galactic.testutils.AuthForTest;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
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
class StellarGetVotingsOfVoterTest extends StellarBaseTest {
    @Inject
    AuthForTest auth;

    @Test
    public void testGetVotingsOfVoterNotAuthenticated() {
        Log.info("[START TEST]: testGetVotingsOfVoterNotAuthenticated()");

        given()
                .get(rest.voting.url + "/")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());

        Log.info("[  END TEST]: testGetVotingsOfVoterNotAuthenticated()\n\n");
    }

    @Test
    public void testGetVotingsOfVoterWithPaging() {
        Log.info("[START TEST]: testGetVotingsOfVoterWithPaging()");

        var createdVotingIds = createMultipleVotingsForPaging();

        var votingIdsWhereAliceIsExpectedAsParticipant = createdVotingIds.stream()
                .filter(id -> id % 2 == 0)
                .toList()
                .toArray(new Long[]{});
        var actualVotingIdsWhereAliceIsParticipant = getActualVotingIdsWhereAliceIsParticipantWithPaging();
        assertThat(actualVotingIdsWhereAliceIsParticipant, hasItems(votingIdsWhereAliceIsExpectedAsParticipant));

        var votingIdsWhereAliceIsNotExpectedAsParticipant = createdVotingIds.stream()
                .filter(id -> id % 2 == 1)
                .toList()
                .toArray(new Long[]{});
        assertThat(actualVotingIdsWhereAliceIsParticipant, not(hasItems(votingIdsWhereAliceIsNotExpectedAsParticipant)));

        Log.info("[  END TEST]: testGetVotingsOfVoterWithPaging()\n\n");
    }

    @Test
    public void testGetVotingsOfVoterInvalidPage() {
        Log.info("[START TEST]: testGetVotingsOfVoterInvalidPage()");

        createMultipleVotingsForPaging();

        var withAccessToken = auth.loginAs("alice");
        int totalPages = getTotalPageCount();

        given()
                .auth().oauth2(withAccessToken)
                .get(rest.voting.url + "/?page=" + totalPages)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("totalPages", greaterThan(0))
                .body("items", hasSize(0));
        ;

        Log.info("[  END TEST]: testGetVotingsOfVoterInvalidPage()\n\n");
    }

    private List<Long> createMultipleVotingsForPaging() {
        int numOfTotalVotingsToCreate = 42;
        List<Long> votingIds = new ArrayList<>();

        for (int i = 0; i < numOfTotalVotingsToCreate; i++) {
            long votingId = this.createAVotingAsCharlieWithParticipantAsAlice();
            votingIds.add(votingId);
        }

        return votingIds;
    }

    private long createAVotingAsCharlieWithParticipantAsAlice() {
        var createRequest = rest.voting.makeCreateRequest();
        var withAccessToken = auth.loginAs("charlie");

        var location = given()
                .auth().oauth2(withAccessToken)
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post(rest.voting.url)
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .extract()
                .header("Location");

        String[] locationParts = location.split("/");
        long votingId = Long.parseLong(locationParts[locationParts.length - 1]);
        if (votingId % 2 == 0) {
            addAliceAsParticipantTo(votingId);
        }

        return votingId;
    }

    private int getTotalPageCount() {
        return rest.getPage(rest.voting.url.toString(), "alice", 0).totalPages();
    }

    private void addAliceAsParticipantTo(Long votingId) {
        var addVotersRequest = new AddVotersRequest(List.of("alice@galactic.pub"));
        var withAccessTokenForAlice = auth.loginAs("charlie");
        given()
                .auth().oauth2(withAccessTokenForAlice)
                .contentType(ContentType.JSON)
                .body(addVotersRequest)
                .post(rest.voting.url + "/addvoters/" + votingId)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    private List<Long> getActualVotingIdsWhereAliceIsParticipantWithPaging() {
        List<PageResponse> aliceAsParticipantResponses = rest.getPages(rest.voting.url.toString(), "alice");
        return aliceAsParticipantResponses.stream()
                .map(r -> rest.getIdsFrom(r))
                .flatMap(Collection::stream)
                .toList();
    }
}
