package host.galactic.stellar;

import host.galactic.data.entities.VotingEntity;
import jakarta.persistence.EntityManager;

public class StellarBaseTestDb {
    public EntityManager entityManager;

    public StellarBaseTestDb(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean areAssetAccountsCreatedFor(Long votingId) {
        var voting = entityManager.find(VotingEntity.class, votingId);
        return voting.distributionAccountSecret != null;
    }

    public boolean areChannelAccountsCreatedFor(Long votingId) {
        var voting = entityManager.find(VotingEntity.class, votingId);
        return voting.maxVoters == voting.channelAccounts.size();
    }

    public void deleteAllVoting() {
        var votings = entityManager.createQuery("select v from VotingEntity v", VotingEntity.class)
                .getResultList();
        votings.forEach(v -> entityManager.remove(v));
    }
}
