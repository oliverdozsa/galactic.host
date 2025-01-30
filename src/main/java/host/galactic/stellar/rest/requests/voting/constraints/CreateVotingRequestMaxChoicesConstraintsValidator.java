package host.galactic.stellar.rest.requests.voting.constraints;

import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CreateVotingRequestMaxChoicesConstraintsValidator implements
        ConstraintValidator<CreateVotingRequestMaxChoicesConstraints, CreateVotingRequest> {
    @Override
    public boolean isValid(CreateVotingRequest createVotingRequest, ConstraintValidatorContext constraintValidatorContext) {
        if(createVotingRequest.ballotType() == CreateVotingRequest.BallotType.MULTI_CHOICE) {
            return createVotingRequest.maxChoices() != null && createVotingRequest.maxChoices() > 0;
        }

        return true;
    }
}
