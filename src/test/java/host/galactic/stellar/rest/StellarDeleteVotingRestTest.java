package host.galactic.stellar.rest;

import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.StellarBaseTest;
import host.galactic.testutils.AuthForTest;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@QuarkusTest
public class StellarDeleteVotingRestTest extends StellarBaseTest {
    @Inject
    AuthForTest auth;

    @Test
    public void testDeleteExisting() {
        Log.info("[START TEST]: testDeleteExisting()");

        long id = rest.voting.createAs("alice");
        assertVotingWithIdExists(id);

        var asAlice = auth.loginAs("alice");
        given().auth().oauth2(asAlice)
                .when()
                .delete(rest.voting.url + "/" + id)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        assertVotingWithIdDoesNotExist(id);

        Log.info("[  END TEST]: testDeleteExisting()");
    }

    @Test
    public void testDeleteNonExisting() {
        Log.info("[START TEST]: testDeleteNonExisting()");

        assertVotingWithIdDoesNotExist(-1);

        var asAlice = auth.loginAs("alice");
        given().auth().oauth2(asAlice)
                .when()
                .delete(rest.voting.url + "/-1")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        Log.info("[  END TEST]: testDeleteNonExisting()");
    }

    @Test
    public void testDeleteForbidden() {
        Log.info("[START TEST]: testDeleteForbidden()");

        long id = rest.voting.createAs("alice");
        assertVotingWithIdExists(id);

        var asBob = auth.loginAs("bob");
        given().auth().oauth2(asBob)
                .when()
                .delete(rest.voting.url + "/" + id)
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

        assertVotingWithIdExists(id);

        Log.info("[  END TEST]: testDeleteForbidden()");
    }

    private void assertVotingWithIdExists(long id) {
        VotingEntity voting = null;

        try {
            voting = db.entityManager.createQuery("select v from VotingEntity v where v.id = :id", VotingEntity.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ignored) {
        }

        assertThat("Voting with id " + id + " should exist!", voting, notNullValue());
    }

    private void assertVotingWithIdDoesNotExist(long id) {
        VotingEntity voting = null;

        try {
            voting = db.entityManager.createQuery("select v from VotingEntity v where v.id = :id", VotingEntity.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ignored) {
        }

        assertThat("Voting with id " + id + " should not exist!", voting, nullValue());
    }
}
