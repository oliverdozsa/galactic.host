package host.galactic.data.mappers;

import host.galactic.data.entities.*;
import host.galactic.stellar.rest.requests.voting.CreatePollOptionRequest;
import host.galactic.stellar.rest.requests.voting.CreatePollRequest;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;

import java.time.Instant;
import java.util.ArrayList;

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
        votingEntity.polls = new ArrayList<>();

        for (int i = 0; i < request.polls().size(); i++) {
            addPollRequestTo(votingEntity, request.polls().get(i), i + 1);
        }

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

    private static void addPollRequestTo(VotingEntity entity, CreatePollRequest request, int index) {
        VotingPollEntity pollEntity = new VotingPollEntity();

        pollEntity.description = request.description();
        pollEntity.index = index;
        pollEntity.question = request.question();
        pollEntity.options = new ArrayList<>();

        request.options().forEach(o -> addPollOptionTo(pollEntity, o));

        entity.polls.add(pollEntity);
    }

    private static void addPollOptionTo(VotingPollEntity pollEntity, CreatePollOptionRequest request) {
        VotingPollOptionEntity pollOptionEntity = new VotingPollOptionEntity();

        pollOptionEntity.code = request.code();
        pollOptionEntity.name = request.name();

        pollEntity.options.add(pollOptionEntity);
    }
}
