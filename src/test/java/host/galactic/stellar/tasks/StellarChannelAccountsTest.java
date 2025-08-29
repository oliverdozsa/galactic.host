package host.galactic.stellar.tasks;

import host.galactic.stellar.rest.StellarRestTestBase;
import host.galactic.stellar.rest.requests.voting.AddVotersRequest;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class StellarChannelAccountsTest extends StellarRestTestBase {
    @Test
    public void testChannelAccountsCreated() {
        Log.info("[START TEST]: testChannelAccountsCreated()");

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        createAVotingWithThreeParticipants();
        Assertions.fail("Implement testChannelAccountsCreated()");

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
