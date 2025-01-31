package host.galactic.stellar.rest.responses.voting;

import java.util.List;

public record VotingPollResponse(
        Integer index,
        String question,
        String description,
        List<VotingPollOptionResponse> pollOptions
) {
}
