package host.galactic.stellar.rest.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record CreateVotingRequest(
        @NotBlank(message = "Title cannot be blank.")
        @Size(min = 2, max = 1000, message = "Title length must be >= 2 and <= 1000.")
        String title,

        @Size(min = 2, max = 1000, message = "Description length must be >= 2 and <= 1000.")
        String description,

        @NotNull(message = "Max voters must not be null.")
        @Min(value = 2, message = "There must be at least 2 voters.")
        @Max(value = 500, message = "There can be at most 500 voters per voting.")
        Integer maxVoters,

        @NotBlank(message = "Token ID cannot be blank.")
        @Size(min = 2, max = 8, message = "Token ID's length must be >= 2 and <= 8.")
        @Pattern(regexp = "^[0-9a-z]+$", message = "Token ID must consists of number and / or lowercase letters.")
        String tokenId,

        @Valid
        CreateVotingRequestDates dates
) {
}
