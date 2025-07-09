package host.galactic.stellar.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.rest.requests.voting.AddVotersRequest;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import host.galactic.stellar.rest.responses.voting.PageResponse;
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
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class StellarGetVotingsOfVoterTest {
    @TestHTTPEndpoint(StellarVotingRest.class)
    @TestHTTPResource
    private URL stellarVotingRest;

    private AuthForTest authForTest = new AuthForTest();

    @Test
    public void testGetVotingsOfVoterNotAuthenticated() {
        Log.info("[START TEST]: testGetVotingsOfVoterNotAuthenticated()");

        given()
                .get(stellarVotingRest + "/")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());

        Log.info("[  END TEST]: testGetVotingsOfVoterNotAuthenticated()\n\n");
    }

    @Test
    public void testGetVotingsOfVoterWithPaging() {
        Log.info("[START TEST]: testGetVotingsOfVoterWithPaging()");

        int totalVotes = createMultipleVotingsForPaging();
        int expectedNumOfVotesWhereAliceParticipates = totalVotes / 2;

        String withAccessToken = authForTest.loginAs("alice");
        var pageResponse = given()
                .auth().oauth2(withAccessToken)
                .get(stellarVotingRest + "/?page=0")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("totalPages", equalTo(2))
                .body("items", hasSize(15))
                .extract()
                .body()
                .as(PageResponse.class);

        int currentPage = 1;
        int actualNumOfVotesWhereAliceParticipates = 15;
        while (currentPage < pageResponse.totalPages()) {
            pageResponse = given()
                    .auth().oauth2(withAccessToken)
                    .get(stellarVotingRest + "/?page=" + currentPage)
                    .then()
                    .statusCode(Response.Status.OK.getStatusCode())
                    .extract()
                    .body()
                    .as(PageResponse.class);

            actualNumOfVotesWhereAliceParticipates += pageResponse.items().size();

            currentPage += 1;
        }

        assertThat(expectedNumOfVotesWhereAliceParticipates, equalTo(actualNumOfVotesWhereAliceParticipates));

        Log.info("[  END TEST]: testGetVotingsOfVoterWithPaging()\n\n");
    }

    @Test
    public void testGetVotingsOfVoterInvalidPage() {
        Log.info("[START TEST]: testGetVotingsOfVoterInvalidPage()");

        createMultipleVotingsForPaging();

        String withAccessToken = authForTest.loginAs("alice");
        int totalPages = getTotalPageCount();

        given()
                .auth().oauth2(withAccessToken)
                .get(stellarVotingRest + "/?page=" + totalPages)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("totalPages", greaterThan(0))
                .body("items", hasSize(0));
        ;

        Log.info("[  END TEST]: testGetVotingsOfVoterInvalidPage()\n\n");
    }

    private int createMultipleVotingsForPaging() {
        int numOfTotalVotingsToCreate = 42;

        for (int i = 0; i < numOfTotalVotingsToCreate; i++) {
            this.createAVotingAsCharlieWithParticipantAsAlice();
        }

        return numOfTotalVotingsToCreate;
    }

    private void createAVotingAsCharlieWithParticipantAsAlice() {
        CreateVotingRequest createRequest = makeCreateVotingRequest();
        String withAccessToken = authForTest.loginAs("charlie");

        String location = given()
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
        long votingId = Long.parseLong(locationParts[locationParts.length - 1]);
        if (votingId % 2 == 0) {
            addAliceAsParticipantTo(votingId);
        }
    }

    private CreateVotingRequest makeCreateVotingRequest() {
        ObjectNode votingRequestJson = JsonUtils.readJsonFile("valid-voting-request.json");
        return JsonUtils.convertJsonNodeTo(CreateVotingRequest.class, votingRequestJson);
    }

    private int getTotalPageCount() {
        String withAccessToken = authForTest.loginAs("alice");

        return given()
                .auth().oauth2(withAccessToken)
                .get(stellarVotingRest + "/")
                .then()
                .extract()
                .body()
                .as(PageResponse.class)
                .totalPages();
    }

    private void addAliceAsParticipantTo(Long votingId) {
        AddVotersRequest addVotersRequest = new AddVotersRequest(List.of("alice@galactic.pub"));
        String withAccessTokenForAlice = authForTest.loginAs("charlie");
        given()
                .auth().oauth2(withAccessTokenForAlice)
                .contentType(ContentType.JSON)
                .body(addVotersRequest)
                .post(stellarVotingRest + "/addvoters/" + votingId)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}
