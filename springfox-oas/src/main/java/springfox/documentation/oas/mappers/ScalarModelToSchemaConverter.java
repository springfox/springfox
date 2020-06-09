package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.media.*;
import org.springframework.core.convert.converter.Converter;
import springfox.documentation.schema.ScalarModelSpecification;
import springfox.documentation.schema.ScalarType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ScalarModelToSchemaConverter implements Converter<ScalarModelSpecification, Schema<?>> {
    private static final Map<ScalarType, Function<ScalarModelSpecification, Schema<?>>> SCALAR_SCHEMA_FACTORY =
            new HashMap<ScalarType, Function<ScalarModelSpecification, Schema<?>>>() {{
                put(ScalarType.STRING, any -> new StringSchema());
                put(ScalarType.BYTE, any -> new ByteArraySchema());
                put(ScalarType.INTEGER, any -> new IntegerSchema());
                put(ScalarType.LONG, any -> new IntegerSchema().format("int64"));
                put(ScalarType.BIGINTEGER, any -> new NumberSchema());
                put(ScalarType.BIGDECIMAL, any -> new NumberSchema());
                put(ScalarType.DOUBLE, any -> new NumberSchema());
                put(ScalarType.FLOAT, any -> new NumberSchema());
                put(ScalarType.BOOLEAN, any -> new BooleanSchema());
                put(ScalarType.BINARY, any -> new BinarySchema());
                put(ScalarType.DATE, any -> new DateSchema());
                put(ScalarType.DATE_TIME, any -> new DateTimeSchema());
                put(ScalarType.UUID, any -> new UUIDSchema());
            }};

    @Override
    public Schema<?> convert(ScalarModelSpecification source) {
        return SCALAR_SCHEMA_FACTORY
                .getOrDefault(source.getType(), any -> null)
                .apply(source);
    }
}
