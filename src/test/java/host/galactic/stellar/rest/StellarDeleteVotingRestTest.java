package host.galactic.stellar.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import host.galactic.testutils.AuthForTest;
import host.galactic.testutils.JsonUtils;
import io.quarkus.logging.Log;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
public class StellarDeleteVotingRestTest {
    @TestHTTPEndpoint(StellarVotingRest.class)
    @TestHTTPResource
    private URL stellarVotingRest;

    private AuthForTest authForTest = new AuthForTest();

    @Test
    public void testDeleteExisting() {
        Log.info("[START TEST]: testDeleteExisting()");

//        long id = createAVotingAs("alice");
//        assertVotingWithIdExists(id);
//
//        String token = authForTest.loginAs("alice");
//        given().auth().oauth2(token)
//                .when()
//                .delete(stellarVotingRest + "/" + id)
//                .then()
//                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
//
//        assertVotingWithIdDoesNotExist(id);

        Log.info("[  END TEST]: testDeleteExisting()");
    }

    @Test
    public void testDeleteNonExisting() {
        Log.info("[START TEST]: testDeleteNonExisting()");

//        String token = authForTest.loginAs("alice");
//        given().auth().oauth2(token)
//                .when()
//                .delete(stellarVotingRest + "/-1")
//                .then()
//                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        Log.info("[  END TEST]: testDeleteNonExisting()");
    }

    @Test
    public void testDeleteNotAuthorized() {
        Log.info("[START TEST]: testDeleteNotAuthorized()");

//        long id = createAVotingAs("alice");
//        assertVotingWithIdExists(id);
//
//        String token = authForTest.loginAs("bob");
//        given().auth().oauth2(token)
//                .when()
//                .delete(stellarVotingRest + "/" + id)
//                .then()
//                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
//
//        assertVotingWithIdExists(id);

        Log.info("[  END TEST]: testDeleteNotAuthorized()");
    }

    private void assertVotingWithIdExists(long id) {
        // TODO
    }

    private void assertVotingWithIdDoesNotExist(long id) {
        // TODO
    }
}
