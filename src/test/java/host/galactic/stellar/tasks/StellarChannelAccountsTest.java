package host.galactic.stellar.tasks;

import host.galactic.data.entities.ChannelAccountEntity;
import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.rest.StellarRestTestBase;
import host.galactic.stellar.rest.requests.voting.AddVotersRequest;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import host.galactic.testutils.JsonUtils;
import host.galactic.testutils.StringUtils;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.awaitility.Awaitility.*;

@QuarkusTest
public class StellarChannelAccountsTest extends StellarRestTestBase {
    @Inject
    EntityManager entityManager;

    @BeforeEach
    @Transactional
    public void deleteAllVotings() {
        var votings = entityManager.createQuery("select v from VotingEntity v", VotingEntity.class)
                .getResultList();
        votings.forEach(v -> entityManager.remove(v));
    }

    @Test
    public void testChannelAccountsCreated() throws InterruptedException {
        Log.info("[START TEST]: testChannelAccountsCreated()");

        var votingId = createAVotingWithMultipleParticipants();
        await().until(() -> channelAccountsOfVoting(votingId), hasSize(42));

        Log.info("[  END TEST]: testChannelAccountsCreated()");
    }

    @Transactional
    public List<ChannelAccountEntity> channelAccountsOfVoting(Long votingId) {
        return entityManager.createQuery("select c from ChannelAccountEntity c where voting.id = :id", ChannelAccountEntity.class)
                .setParameter("id", votingId)
                .getResultList();
    }

    private long createAVotingWithMultipleParticipants() {
        var votingRequestJson = JsonUtils.readJsonFile("valid-voting-request.json");
        votingRequestJson.put("maxVoters", 42);

        var createVotingRequest = JsonUtils.convertJsonNodeTo(CreateVotingRequest.class, votingRequestJson);

        var votingId = createVoting(createVotingRequest, "alice");

        List<String> multipleParticipants = new ArrayList<>();
        for (int i = 0; i < 42; i++) {
            var randomEmail = StringUtils.generateRandomStringOfLength(10) + "@galactic.pub";
            multipleParticipants.add(randomEmail);
        }

        var addVotersRequest = new AddVotersRequest(multipleParticipants);
        String withAccessTokenForAlice = authForTest.loginAs("alice");
        given()
                .auth().oauth2(withAccessTokenForAlice)
                .contentType(ContentType.JSON)
                .body(addVotersRequest)
                .post(stellarVotingRest + "/addvoters/" + votingId)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        return votingId;
    }
}
