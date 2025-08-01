package host.galactic.stellar.rest.requests.voting.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = CreateVotingRequestFundingAccountConstraintsValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
@Documented
public @interface CreateVotingRequestFundingAccountConstraints {
    String message() default "Must be a valid stellar secret seed.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
