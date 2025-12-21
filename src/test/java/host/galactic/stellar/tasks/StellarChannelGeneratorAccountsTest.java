package host.galactic.stellar.tasks;

import host.galactic.data.entities.ChannelGeneratorEntity;
import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.StellarBaseTest;
import host.galactic.stellar.rest.requests.voting.AddVotersRequest;
import host.galactic.testutils.AuthForTest;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.awaitility.Awaitility.*;

@QuarkusTest
public class StellarChannelGeneratorAccountsTest extends StellarBaseTest {
    @Inject
    private AuthForTest auth;

    @BeforeEach
    @Transactional
    public void deleteAllVotings() {
        db.deleteAllVoting();
    }

    @Test
    public void testChannelGeneratorAccountsCreated() {
        Log.info("[START TEST]: testChannelGeneratorAccountsCreated()");

        var votingId = createAVotingWithThreeParticipants();
        await().until(() -> channelGeneratorsOf(votingId), hasSize(greaterThan(0)));

        Log.info("[  END TEST]: testChannelGeneratorAccountsCreated()");
    }

    @Transactional
    public List<ChannelGeneratorEntity> channelGeneratorsOf(Long votingId) {
        return db.entityManager.createQuery("select c from ChannelGeneratorEntity c where voting.id = :id", ChannelGeneratorEntity.class)
                .setParameter("id", votingId)
                .getResultList();
    }

    private long createAVotingWithThreeParticipants() {
        var id = rest.voting.createAs("alice");

        var addVotersRequest = new AddVotersRequest(List.of("emily@galactic.pub", "duke@galactic.pub", "alice@galactic.pub"));
        String withAccessTokenForAlice = auth.loginAs("alice");
        given()
                .auth().oauth2(withAccessTokenForAlice)
                .contentType(ContentType.JSON)
                .body(addVotersRequest)
                .post(rest.voting.url + "/addvoters/" + id)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        return id;
    }
}
