package host.galactic.data.mappers;

import host.galactic.data.entities.BallotType;
import host.galactic.data.entities.Visibility;
import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.rest.requests.createvoting.CreateVotingRequest;

import java.time.Instant;

public class CreateVotingRequestMapper {
    public static VotingEntity from(CreateVotingRequest request) {
        VotingEntity votingEntity = new VotingEntity();

        votingEntity.ballotType = from(request.ballotType());
        votingEntity.createdAt = Instant.now();
        // TODO: use when auth is present
        votingEntity.createdBy = "<ANONYMOUS>";
        votingEntity.title = request.title();
        votingEntity.description = request.description();
        votingEntity.encryptedUntil = request.dates().encryptedUntil();
        votingEntity.startDate = request.dates().startDate();
        votingEntity.endDate = request.dates().endDate();
        votingEntity.maxChoices = request.maxChoices();
        votingEntity.maxVoters = request.maxVoters();
        votingEntity.visibility = from(request.visibility());
        votingEntity.assetCode = request.tokenId();
        votingEntity.userGivenFundingAccountSecret = request.fundingAccountSecret();
        votingEntity.isOnTestNetwork = request.useTestNet();

        return votingEntity;
    }

    private static BallotType from(CreateVotingRequest.BallotType requestBallotType) {
        return switch (requestBallotType) {
            case MULTI_POLL -> BallotType.MULTI_POLL;
            case MULTI_CHOICE -> BallotType.MULTI_CHOICE;
        };
    }

    private static Visibility from(CreateVotingRequest.Visibility requestVisibilityType) {
        return switch (requestVisibilityType) {
            case UNLISTED -> Visibility.UNLISTED;
            case PRIVATE -> Visibility.PRIVATE;
        };
    }
}
