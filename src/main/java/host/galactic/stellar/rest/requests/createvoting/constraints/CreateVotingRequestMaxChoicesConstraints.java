package host.galactic.stellar.rest.requests.createvoting.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = CreateVotingRequestMaxChoicesConstraintsValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface CreateVotingRequestMaxChoicesConstraints {
    String message() default "Max choices must be >= 1 if ballot type is MULTI_CHOICE.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
