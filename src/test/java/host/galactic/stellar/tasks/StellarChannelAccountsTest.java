package host.galactic.stellar.tasks;

import host.galactic.data.entities.ChannelGeneratorEntity;
import host.galactic.stellar.rest.StellarRestTestBase;
import host.galactic.stellar.rest.requests.voting.AddVotersRequest;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class StellarChannelAccountsTest extends StellarRestTestBase {
    @Inject
    EntityManager entityManager;

    @Test
    public void testChannelAccountsCreated() {
        Log.info("[START TEST]: testChannelAccountsCreated()");

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        var votingId = createAVotingWithThreeParticipants();

        var channelGenerators = entityManager.createQuery("select c from ChannelGeneratorEntity c where voting.id = :id", ChannelGeneratorEntity.class)
                .setParameter("id", votingId)
                .getResultList();

        assertThat(channelGenerators, hasSize(greaterThan(0)));

        // TODO: check for the actual channel accounts too.

        Log.info("[  END TEST]: testChannelAccountsCreated()");
    }

    @Test
    public void testChannelAccountsAreRefundedWhenVotingIsDeleted() {
        Log.info("[START TEST]: testChannelAccountsAreRefundedWhenVotingIsDeleted()");

        createAVotingWithThreeParticipants();
        Assertions.fail("Implement testChannelAccountsAreRefundedWhenVotingIsDeleted()");

        Log.info("[  END TEST]: testChannelAccountsAreRefundedWhenVotingIsDeleted()");
    }

    @Test
    public void testChannelAccountsAreRefundedWhenVotingEnds() {
        Log.info("[START TEST]: testChannelAccountsAreRefundedWhenVotingEnds()");

        createAVotingWithThreeParticipants();
        Assertions.fail("Implement testChannelAccountsAreRefundedWhenVotingEnds()");

        Log.info("[  END TEST]: testChannelAccountsAreRefundedWhenVotingEnds()");
    }

    private long createAVotingWithThreeParticipants() {
        var id = createAVotingAs("alice");

        AddVotersRequest addVotersRequest = new AddVotersRequest(List.of("emily@galactic.pub", "duke@galactic.pub", "alice@galactic.pub"));
        String withAccessTokenForAlice = authForTest.loginAs("alice");
        given()
                .auth().oauth2(withAccessTokenForAlice)
                .contentType(ContentType.JSON)
                .body(addVotersRequest)
                .post(stellarVotingRest + "/addvoters/" + id)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        return id;
    }
}
