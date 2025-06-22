package host.galactic.stellar.rest.requests.voting.constraints;

import host.galactic.stellar.rest.requests.voting.CreateVotingRequestDates;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CreateVotingRequestsDatesConstraintsValidator implements
        ConstraintValidator<CreateVotingRequestsDatesConstraints, CreateVotingRequestDates> {

    @Override
    public boolean isValid(CreateVotingRequestDates request, ConstraintValidatorContext constraintValidatorContext) {
        if(request.startDate() == null || request.endDate() == null) {
            return false;
        }

        return request.startDate().isBefore(request.endDate());
    }
}
