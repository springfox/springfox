package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.media.BinarySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ByteArraySchema;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import org.springframework.core.convert.converter.Converter;
import springfox.documentation.schema.ScalarModelSpecification;
import springfox.documentation.schema.ScalarType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ScalarModelToSchemaConverter implements Converter<ScalarModelSpecification, Schema<?>> {
  private static final Map<ScalarType, Function<ScalarModelSpecification, Schema<?>>>
      SCALAR_SCHEMA_FACTORY = new HashMap<>();

  static {
    SCALAR_SCHEMA_FACTORY.put(ScalarType.STRING, any -> new StringSchema());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.BYTE, any -> new ByteArraySchema());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.INTEGER, any -> new IntegerSchema());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.LONG, any -> new IntegerSchema().format("int64"));
    SCALAR_SCHEMA_FACTORY.put(ScalarType.BIGINTEGER, any -> new NumberSchema());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.BIGDECIMAL, any -> new NumberSchema());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.DOUBLE, any -> new NumberSchema());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.FLOAT, any -> new NumberSchema());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.BOOLEAN, any -> new BooleanSchema());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.BINARY, any -> new BinarySchema());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.DATE, any -> new DateSchema());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.DATE_TIME, any -> new DateTimeSchema());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.UUID, any -> new UUIDSchema());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.URL, any -> new StringSchema().format("url"));
    SCALAR_SCHEMA_FACTORY.put(ScalarType.URI, any -> new StringSchema().format("uri"));
    SCALAR_SCHEMA_FACTORY.put(ScalarType.OBJECT, any -> new ObjectSchema());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.PASSWORD, any -> new StringSchema().format("password"));
    SCALAR_SCHEMA_FACTORY.put(ScalarType.EMAIL, any -> new StringSchema().format("email"));
    SCALAR_SCHEMA_FACTORY.put(ScalarType.CURRENCY, any -> new NumberSchema().format("bigdecimal"));
  }

  @Override
  public Schema<?> convert(ScalarModelSpecification source) {
    return SCALAR_SCHEMA_FACTORY
        .getOrDefault(source.getType(), any -> null)
        .apply(source);
  }
}
