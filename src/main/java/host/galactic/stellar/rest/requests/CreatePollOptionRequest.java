package host.galactic.stellar.rest.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePollOptionRequest(
        @NotBlank(message = "Name cannot be null.")
        @Size(min = 2, max = 300, message = "Name length must be >= 2 and <= 300.")
        String name
) {
}
