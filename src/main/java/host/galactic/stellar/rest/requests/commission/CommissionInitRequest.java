package host.galactic.stellar.rest.requests.commission;

import jakarta.validation.constraints.NotNull;

public record CommissionInitRequest(
        @NotNull(message = "Voting ID cannot be null!")
        Long votingId
) {
}
