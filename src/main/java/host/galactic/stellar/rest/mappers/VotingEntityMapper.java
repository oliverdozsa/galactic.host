package host.galactic.stellar.rest.mappers;

import host.galactic.data.entities.VotingEntity;
import host.galactic.data.entities.VotingPollEntity;
import host.galactic.data.entities.VotingPollOptionEntity;
import host.galactic.stellar.operations.StellarUtils;
import host.galactic.stellar.rest.responses.voting.VotingPollOptionResponse;
import host.galactic.stellar.rest.responses.voting.VotingPollResponse;
import host.galactic.stellar.rest.responses.voting.VotingResponse;
import org.stellar.sdk.KeyPair;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static host.galactic.stellar.operations.StellarUtils.toAccountId;

public class VotingEntityMapper {
    public static VotingResponse from(VotingEntity entity) {
        var polls = pollsFrom(entity);

        var fundingAccountId = toAccountId(entity.fundingAccountSecret);
        var distributionAccountId = entity.distributionAccountSecret == null ? "" : toAccountId(entity.distributionAccountSecret);
        var ballotAccountId = entity.ballotAccountSecret == null ? "" : toAccountId(entity.ballotAccountSecret);
        var issuerAccountId = entity.issuerAccountSecret == null ? "" :toAccountId(entity.issuerAccountSecret);

        return new VotingResponse(
                entity.id,
                entity.title,
                entity.description,
                entity.maxVoters,
                entity.createdAt,
                encryptionKeyOrBlankFrom(entity),
                entity.startDate,
                entity.endDate,
                entity.encryptedUntil,
                entity.assetCode,
                entity.visibility.name(),
                entity.ballotType.name(),
                entity.maxChoices,
                entity.isOnTestNetwork,
                polls,
                entity.numOfVoters,
                fundingAccountId,
                distributionAccountId,
                ballotAccountId,
                issuerAccountId
        );
    }

    private static String encryptionKeyOrBlankFrom(VotingEntity entity) {
        if (entity.encryptedUntil != null && entity.encryptedUntil.isBefore(Instant.now())) {
            return entity.encryptionKey;
        }

        return "";
    }

    private static List<VotingPollResponse> pollsFrom(VotingEntity entity) {
        return entity.polls.stream()
                .map(VotingEntityMapper::from)
                .collect(Collectors.toList());
    }

    private static VotingPollResponse from(VotingPollEntity pollEntity) {
        var options = optionsFrom(pollEntity);

        return new VotingPollResponse(
                pollEntity.index,
                pollEntity.question,
                pollEntity.description,
                options
        );
    }

    private static List<VotingPollOptionResponse> optionsFrom(VotingPollEntity pollEntity) {
        return pollEntity.options.stream()
                .map(VotingEntityMapper::from)
                .collect(Collectors.toList());
    }

    private static VotingPollOptionResponse from(VotingPollOptionEntity entity) {
        return new VotingPollOptionResponse(entity.name, entity.code);
    }
}
