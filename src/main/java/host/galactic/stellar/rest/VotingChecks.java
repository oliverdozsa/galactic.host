package host.galactic.stellar.rest;

import host.galactic.data.entities.VotingEntity;

public class VotingChecks {
    public static boolean doesUserNotParticipateIn(VotingEntity entity, String userEmail) {
        return entity.voters.stream()
                .map(u -> u.email)
                .noneMatch(e -> e.equals(userEmail));
    }

    public static boolean doesUserParticipateIn(VotingEntity entity, String userEmail) {
        return !doesUserNotParticipateIn(entity, userEmail);
    }

    public static boolean areAssetAccountsCreated(VotingEntity entity) {
        return entity.ballotAccountSecret != null;
    }
}
