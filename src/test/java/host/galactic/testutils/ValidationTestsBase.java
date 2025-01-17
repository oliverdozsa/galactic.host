package host.galactic.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ValidationTestsBase {
    @Inject
    protected Validator validator;

    private ObjectMapper mapper = new ObjectMapper();

    public ValidationTestsBase() {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    protected static <T> List<String> extractViolationMessages(Set<ConstraintViolation<T>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();
    }

    protected ObjectNode readJsonFile(String fileName) {
        File jsonFile = new File(
                Objects.requireNonNull(this.getClass().getClassLoader().getResource(fileName)).getFile());
        try {
            return (ObjectNode) mapper.readTree(jsonFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> T convertJsonNodeTo(Class<T> klass, JsonNode json) {
        try {
            return mapper.treeToValue(json, klass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
