package host.galactic.stellar.tasks;

import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.StellarTestBase;
import host.galactic.stellar.rest.responses.voting.VotingResponse;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.awaitility.Awaitility.*;

@QuarkusTest
public class StellarAssetAccountsTest {
    @Inject
    private StellarTestBase testBase;

    @BeforeEach
    @Transactional
    public void deleteAllVotings() {
        var votings = testBase.db.entityManager.createQuery("select v from VotingEntity v", VotingEntity.class)
                .getResultList();
        votings.forEach(v -> testBase.db.entityManager.remove(v));
    }

    @Test
    public void testAssetAccountsCreated() {
        Log.info("[START TEST]: testAssetAccountsCreated()");

        var votingId = testBase.rest.voting.createAVotingAs("alice");
        await().until(() -> testBase.rest.voting.getVotingBy(votingId, "alice"), hasAssetAccounts());

        Log.info("[  END TEST]: testAssetAccountsCreated()");
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
