package requests.social;

import com.networknt.schema.*;

import javax.inject.Provider;

public class ActivityJsonSchemaValidatorProvider implements Provider<JsonSchema> {
    private JsonSchemaFactory schemaFactory;
    private SchemaValidatorsConfig config;

    public ActivityJsonSchemaValidatorProvider() {
        schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012, builder ->
                builder.schemaMappers(schemaMapper -> schemaMapper.mapPrefix("https://galactic.pub/as2-schema/", "classpath:as2schema/")));

        config = new SchemaValidatorsConfig();
        config.setPathType(PathType.JSON_PATH);
    }

    @Override
    public JsonSchema get() {
        return schemaFactory.getSchema(SchemaLocation.of("https://galactic.pub/as2-schema/activity.json"), config);
    }
}
