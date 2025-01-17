package host.galactic.stellar.rest.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePollRequest(
        @NotBlank
        @Size(min = 2, max = 1000, message = "Question length must be >= 2 and <= 1000.")
        String question
) {
}
