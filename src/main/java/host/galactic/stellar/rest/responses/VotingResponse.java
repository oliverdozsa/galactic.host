package host.galactic.stellar.rest.responses;

import java.time.Instant;

public record VotingResponse (
        Long id,
        String title,
        String description,
        Integer maxVoters,
        Instant createdAt,
        String decryptionKey,
        Instant startDate,
        Instant endDate,
        String assetCode,
        String visibility,
        String ballotType,
        Integer maxChoices,
        Boolean isOnTestNetwork
) {
}
