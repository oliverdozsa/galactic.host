package host.galactic.stellar.rest.requests;

import jakarta.validation.constraints.*;

public record CreateVotingRequest(
        @NotBlank(message = "Title cannot be blank")
        @Size(min = 2, max = 1000, message = "Title length must be >= 2 and <= 1000")
        String title,

        @NotNull(message = "Max voters must be given.")
        @Min(value = 2, message = "There must be at least 2 voters.")
        @Max(value = 500, message = "There can be at most 500 voters per voting.")
        Integer maxVoters
) {
}
