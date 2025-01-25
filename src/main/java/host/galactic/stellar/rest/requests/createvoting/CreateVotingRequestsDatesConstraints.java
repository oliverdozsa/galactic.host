package host.galactic.stellar.rest.requests.createvoting;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = CreateVotingRequestsDatesConstraintsValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface CreateVotingRequestsDatesConstraints {
    String message() default "Start date must be before end date.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
