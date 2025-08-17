package host.galactic.stellar.rest;

import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class StellarDeleteVotingRestTest extends StellarRestTestBase {

    @Test
    public void testDeleteExisting() {
        Log.info("[START TEST]: testDeleteExisting()");

        long id = createAVotingAs("alice");
        assertVotingWithIdExists(id);

        String asAlice = authForTest.loginAs("alice");
        given().auth().oauth2(asAlice)
                .when()
                .delete(stellarVotingRest + "/" + id)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        assertVotingWithIdDoesNotExist(id);

        Log.info("[  END TEST]: testDeleteExisting()");
    }

    @Test
    public void testDeleteNonExisting() {
        Log.info("[START TEST]: testDeleteNonExisting()");

        assertVotingWithIdDoesNotExist(-1);

        String asAlice = authForTest.loginAs("alice");
        given().auth().oauth2(asAlice)
                .when()
                .delete(stellarVotingRest + "/-1")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        Log.info("[  END TEST]: testDeleteNonExisting()");
    }

    @Test
    public void testDeleteNotAuthorized() {
        Log.info("[START TEST]: testDeleteNotAuthorized()");

        long id = createAVotingAs("alice");
        assertVotingWithIdExists(id);

        String asBob = authForTest.loginAs("bob");
        given().auth().oauth2(asBob)
                .when()
                .delete(stellarVotingRest + "/" + id)
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());

        assertVotingWithIdExists(id);

        Log.info("[  END TEST]: testDeleteNotAuthorized()");
    }

    private void assertVotingWithIdExists(long id) {
        // TODO
    }

    private void assertVotingWithIdDoesNotExist(long id) {
        // TODO
    }
}
