package host.galactic.testutils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class ValidationTestsBase {
    @Inject
    protected Validator validator;

    protected static <T> List<String> extractViolationMessages(Set<ConstraintViolation<T>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();
    }

    protected ObjectNode readJsonFile(String fileName) {
        return JsonUtils.readJsonFile(fileName);
    }

    protected <T> T convertJsonNodeTo(Class<T> klass, JsonNode json) {
        return JsonUtils.convertJsonNodeTo(klass, json);
    }

    protected ArrayNode createArrayNode() {
        return JsonUtils.createArrayNode();
    }

    protected static String generateRandomStringOfLength(int value) {
        String abc = "abcdefghijklmnopqrstuvxyz";
        Random random = new Random();

        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < value; i++) {
            int randomIndex = random.nextInt(abc.length());
            char randomLetter = abc.charAt(randomIndex);
            stringBuilder.append(randomLetter);
        }

        return stringBuilder.toString();
    }
}
