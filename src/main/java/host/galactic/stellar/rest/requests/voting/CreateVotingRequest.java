package host.galactic.stellar.rest.requests.voting;

import host.galactic.stellar.rest.requests.voting.constraints.CreateVotingRequestFundingAccountConstraints;
import host.galactic.stellar.rest.requests.voting.constraints.CreateVotingRequestMaxChoicesConstraints;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

@CreateVotingRequestMaxChoicesConstraints
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

        @NotNull(message = "Visibility cannot be null.")
        Visibility visibility,

        BallotType ballotType,

        Integer maxChoices,

        Boolean useTestNet,

        @NotBlank(message = "Funding account cannot be blank.")
        @CreateVotingRequestFundingAccountConstraints
        String fundingAccountSecret,

        @Valid
        CreateVotingRequestDates dates,

        @Size(min = 1, max = 99, message = "Polls length must be >= 1 and <= 99.")
        List<@Valid CreatePollRequest> polls
) {
    public enum Visibility {
        UNLISTED,
        PRIVATE
    }

    public enum BallotType {
        MULTI_POLL,
        MULTI_CHOICE
    }
}
