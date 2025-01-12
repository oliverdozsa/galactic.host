package host.galactic.stellar.rest.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateVotingRequest(
        @NotBlank(message = "Title cannot be blank")
        @Size(min = 2, max = 1000, message = "Title length must be >= 2 and <= 1000")
        String title
) {
}
