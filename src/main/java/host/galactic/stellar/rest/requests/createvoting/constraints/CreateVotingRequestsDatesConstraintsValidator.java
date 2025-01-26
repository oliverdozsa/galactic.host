package host.galactic.stellar.rest.requests.createvoting.constraints;

import host.galactic.stellar.rest.requests.createvoting.CreateVotingRequestDates;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CreateVotingRequestsDatesConstraintsValidator implements
        ConstraintValidator<CreateVotingRequestsDatesConstraints, CreateVotingRequestDates> {

    @Override
    public boolean isValid(CreateVotingRequestDates request, ConstraintValidatorContext constraintValidatorContext) {
        return request.startDate().isBefore(request.endDate());
    }
}
