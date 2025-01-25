package host.galactic.stellar.rest.requests.createvoting;

import jakarta.validation.constraints.*;

public record CreatePollOptionRequest(
        @NotBlank(message = "Name cannot be blank.")
        @Size(min = 2, max = 300, message = "Name length must be >= 2 and <= 300.")
        String name,

        @NotNull(message = "Code cannot be null.")
        @Min(value = 1, message = "Code value must be >= 1.")
        @Max(value = 99, message = "Code value must be <= 99.")
        Integer code
) {
}
