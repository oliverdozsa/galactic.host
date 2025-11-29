package host.galactic.stellar.tasks;

import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.rest.StellarRestTestBase;
import host.galactic.stellar.rest.responses.voting.VotingResponse;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.*;

@QuarkusTest
public class StellarAssetAccountsTest extends StellarRestTestBase {
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
    public void testAssetAccountsCreated() {
        Log.info("[START TEST]: testAssetAccountsCreated()");

        var votingId = createAVotingAs("alice");
        await().until(() -> getVotingBy(votingId), hasAssetAccounts());

        Log.info("[  END TEST]: testAssetAccountsCreated()");
    }

    public VotingResponse getVotingBy(Long votingId) {
        String withAccessToken = authForTest.loginAs("alice");
        return given()
                .auth().oauth2(withAccessToken)
                .get(stellarVotingRest + "/" + votingId)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(VotingResponse.class);
    }

    private static class VotingAssetAccountsMatcher extends TypeSafeMatcher<VotingResponse> {
        @Override
        protected boolean matchesSafely(VotingResponse votingResponse) {
            return votingResponse.distributionAccountId() != null && !votingResponse.distributionAccountId().isEmpty() &&
                    votingResponse.ballotAccountId() != null && !votingResponse.ballotAccountId().isEmpty() &&
                    votingResponse.issuerAccountId() != null && !votingResponse.issuerAccountId().isEmpty();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("distribution, ballot and issuer account to be present");
        }
    }

    private static VotingAssetAccountsMatcher hasAssetAccounts() {
        return new VotingAssetAccountsMatcher();
    }
}
