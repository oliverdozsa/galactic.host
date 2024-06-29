package components.controllers.social;

import com.networknt.schema.*;
import org.junit.Test;

import java.util.Set;

public class TestSchema {
    @Test
    public void testSchema() {
        JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012, builder ->
                builder.schemaMappers(schemaMapper -> schemaMapper.mapPrefix("https://galactic.pub/as2-schema/", "classpath:as2schema/")));

        SchemaValidatorsConfig config = new SchemaValidatorsConfig();
        config.setPathType(PathType.JSON_PATH);

        JsonSchema schema = jsonSchemaFactory.getSchema(SchemaLocation.of("https://galactic.pub/as2-schema/activity.json"), config);
        System.out.println("schema:" + schema.toString());

        String someCreateActivity = "{\n" +
                "  \"@context\": \"https://www.w3.org/ns/activitystreams\",\n" +
                "  \"type\": \"Create\",\n" +
                "  \"id\": \"https://example.net/~mallory/87374\",\n" +
                "  \"actor\": \"https://example.net/~mallory\",\n" +
                "  \"object\": {\n" +
                "    \"id\": \"https://example.com/~mallory/note/72\",\n" +
                "    \"type\": \"Note\",\n" +
                "    \"attributedTo\": \"https://example.net/~mallory\",\n" +
                "    \"content\": \"This is a note\",\n" +
                "    \"published\": \"2015-02-10T15:04:55Z\",\n" +
                "    \"to\": [\"https://example.org/~john/\"],\n" +
                "    \"cc\": [\"https://example.com/~erik/followers\",\n" +
                "           \"https://www.w3.org/ns/activitystreams#Public\"]\n" +
                "  },\n" +
                "  \"published\": \"2015-02-10T15:04:55Z\",\n" +
                "  \"to\": [\"https://example.org/~john/\"],\n" +
                "  \"cc\": [\"https://example.com/~erik/followers\",\n" +
                "         \"https://www.w3.org/ns/activitystreams#Public\"]\n" +
                "}";

        Set<ValidationMessage> validationMessages = schema.validate(someCreateActivity, InputFormat.JSON);
        validationMessages.forEach(m -> {
            System.out.println(m.getMessageKey() + ": " + m.getMessage());
        });
    }
}
