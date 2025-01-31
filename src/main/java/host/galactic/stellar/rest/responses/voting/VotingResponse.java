package host.galactic.stellar.rest.responses.voting;

import java.time.Instant;
import java.util.List;

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
        Boolean isOnTestNetwork,
        List<VotingPollResponse> polls
) {
}
