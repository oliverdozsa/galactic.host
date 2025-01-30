package host.galactic.stellar.rest.requests.voting;

import host.galactic.stellar.rest.requests.voting.constraints.CreateVotingRequestsDatesConstraints;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@CreateVotingRequestsDatesConstraints
public record CreateVotingRequestDates(
        @Future(message = "Encrypted until must be in the future.")
        Instant encryptedUntil,

        @NotNull(message = "Start date must not be null.")
        Instant startDate,

        @NotNull(message = "End date must not be null.")
        @Future(message = "End date must be in the future.")
        Instant endDate
) {
}
