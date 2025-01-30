package host.galactic.stellar.rest.requests.voting.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.stellar.sdk.KeyPair;


public class CreateVotingRequestFundingAccountConstraintsValidator implements
        ConstraintValidator<CreateVotingRequestFundingAccountConstraints, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try {
            KeyPair.fromSecretSeed(s);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
