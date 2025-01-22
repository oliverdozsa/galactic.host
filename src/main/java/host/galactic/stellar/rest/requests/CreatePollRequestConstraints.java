package host.galactic.stellar.rest.requests;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = CreatePollRequestConstraintsValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface CreatePollRequestConstraints {
    String message() default "Option codes must be unique.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
