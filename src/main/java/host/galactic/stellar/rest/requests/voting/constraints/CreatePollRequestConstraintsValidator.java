package host.galactic.stellar.rest.requests.voting.constraints;

import host.galactic.stellar.rest.requests.voting.CreatePollOptionRequest;
import host.galactic.stellar.rest.requests.voting.CreatePollRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;

public class CreatePollRequestConstraintsValidator implements
        ConstraintValidator<CreatePollRequestConstraints, CreatePollRequest> {
    @Override
    public boolean isValid(CreatePollRequest createPollRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (createPollRequest == null) {
            return false;
        }

        if (createPollRequest.options() == null || createPollRequest.options().isEmpty()) {
            return false;
        }

        int uniqueOptionCodes = countUniqueOptionCodes(createPollRequest);
        return uniqueOptionCodes == createPollRequest.options().size();
    }

    private int countUniqueOptionCodes(CreatePollRequest pollRequest) {
        var uniqueCodes = pollRequest.options().stream()
                .map(CreatePollOptionRequest::code)
                .collect(Collectors.toSet());
        return uniqueCodes.size();
    }
}