package host.galactic.stellar.rest.mappers;

import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.rest.responses.VotingResponse;

import java.time.Instant;

public class VotingEntityMapper {
    public static VotingResponse from(VotingEntity entity) {
        return new VotingResponse(
                entity.id,
                entity.title,
                entity.description,
                entity.maxVoters,
                entity.createdAt,
                encryptionKeyOrBlankFrom(entity),
                entity.startDate,
                entity.endDate,
                entity.assetCode,
                entity.visibility.name(),
                entity.ballotType.name(),
                entity.maxChoices,
                entity.isOnTestNetwork
        );
    }

    private static String encryptionKeyOrBlankFrom(VotingEntity entity) {
        if (entity.encryptedUntil != null && entity.encryptedUntil.isBefore(Instant.now())) {
            return entity.encryptionKey;
        }

        return "";
    }
}
