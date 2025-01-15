package host.galactic.testutils;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Set;

public class ValidationTestsBase {
    @Inject
    protected Validator validator;

    protected static <T> List<String> extractViolationMessages(Set<ConstraintViolation<T>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();
    }
}
