package host.galactic.requests.createvoting;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Set;

class ValidationTestsBase {
    @Inject
    Validator validator;

    protected static <T> List<String> extractViolationMessages(Set<ConstraintViolation<T>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();
    }
}
