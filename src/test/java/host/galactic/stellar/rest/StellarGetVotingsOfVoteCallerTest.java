package host.galactic.stellar.rest;

import host.galactic.testutils.AuthForTest;
import io.quarkus.logging.Log;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.fail;

@QuarkusTest
public class StellarGetVotingsOfVoteCallerTest {
    @TestHTTPEndpoint(StellarVotingRest.class)
    @TestHTTPResource
    private URL stellarVotingRest;

    private AuthForTest authForTest = new AuthForTest();

    @Test
    public void testGetVotingsOfVoteCallerNotAuthenticated() {
        Log.info("[START TEST]: testGetVotingsOfVoteCallerNotAuthenticated()");

        given()
                .get(stellarVotingRest + "/of-vote-caller")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());

        Log.info("[  END TEST]: testGetVotingsOfVoteCallerNotAuthenticated()\n\n");
    }

    @Test
    public void testGetVotingOfVoteCallerPaging() {
        Log.info("[START TEST]: testGetVotingOfVoteCallerPaging()");
        Assertions.fail("Implement test.");
        Log.info("[  END TEST]: testGetVotingOfVoteCallerPaging()\n\n");
    }
}
