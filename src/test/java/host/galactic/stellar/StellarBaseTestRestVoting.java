package host.galactic.stellar;

import host.galactic.stellar.rest.requests.voting.AddVotersRequest;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import host.galactic.stellar.rest.responses.voting.VotingResponse;
import host.galactic.testutils.AuthForTest;
import host.galactic.testutils.JsonUtils;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

public class StellarBaseTestRestVoting {
    private AuthForTest authForTest;

    public URL url;

    public StellarBaseTestRestVoting(AuthForTest authForTest, URL url) {
        this.authForTest = authForTest;
        this.url = url;
    }

    public long createAs(String user) {
        var createRequest = makeCreateRequest();
        return create(createRequest, user);
    }

    public CreateVotingRequest makeCreateRequest() {
        var votingRequestJson = JsonUtils.readJsonFile("valid-voting-request.json");
        return JsonUtils.convertJsonNodeTo(CreateVotingRequest.class, votingRequestJson);
    }

    public long create(CreateVotingRequest request, String user) {
        var withAccessToken = authForTest.loginAs(user);
        var location = given()
                .auth().oauth2(withAccessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(url)
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .extract()
                .header("Location");

        String[] locationParts = location.split("/");
        return Long.parseLong(locationParts[locationParts.length - 1]);
    }

    public VotingResponse getById(Long votingId, String owner) {
        String withAccessToken = authForTest.loginAs(owner);
        return given()
                .auth().oauth2(withAccessToken)
                .get(url + "/" + votingId)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(VotingResponse.class);
    }

    public Long createWithParticipants(String owner, String[] participants) {
        var votingId = createAs(owner);
        addParticipantsTo(votingId, participants, owner);
        return votingId;
    }

    private void addParticipantsTo(Long votingId, String[] voters, String owner) {
        var votersWithEmails = Arrays.stream(voters).map(v -> v + "@galactic.pub")
                .toList();
        var request = new AddVotersRequest(votersWithEmails);

        var withAccessTokenForOwner = authForTest.loginAs(owner);
        given()
                .auth().oauth2(withAccessTokenForOwner)
                .contentType(ContentType.JSON)
                .body(request)
                .post(url + "/addvoters/" + votingId)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

    }
}
