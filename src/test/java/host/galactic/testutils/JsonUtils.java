package host.galactic.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class JsonUtils {
    public static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static ObjectNode readJsonFile(String fileName) {
        var jsonFile = new File(
                Objects.requireNonNull(JsonUtils.class.getClassLoader().getResource(fileName)).getFile());
        try {
            return (ObjectNode) mapper.readTree(jsonFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertJsonNodeTo(Class<T> klass, JsonNode json) {
        try {
            return mapper.treeToValue(json, klass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayNode createArrayNode() {
        return mapper.createArrayNode();
    }
}
