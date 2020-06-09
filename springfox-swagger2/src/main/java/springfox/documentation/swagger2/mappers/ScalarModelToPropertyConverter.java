package springfox.documentation.swagger2.mappers;

import io.swagger.models.properties.BinaryProperty;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.ByteArrayProperty;
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.DateTimeProperty;
import io.swagger.models.properties.DecimalProperty;
import io.swagger.models.properties.DoubleProperty;
import io.swagger.models.properties.FloatProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.LongProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import io.swagger.models.properties.UUIDProperty;
import org.springframework.core.convert.converter.Converter;
import springfox.documentation.schema.ScalarModelSpecification;
import springfox.documentation.schema.ScalarType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ScalarModelToPropertyConverter implements Converter<ScalarModelSpecification, Property> {
  private static final Map<ScalarType, Function<ScalarModelSpecification, Property>> SCALAR_SCHEMA_FACTORY =
      new HashMap<ScalarType, Function<ScalarModelSpecification, Property>>() {{
        put(ScalarType.STRING, any -> new StringProperty());
        put(ScalarType.BYTE, any -> new ByteArrayProperty());
        put(ScalarType.INTEGER, any -> new IntegerProperty());
        put(ScalarType.LONG, any -> {
          IntegerProperty longProperty = new IntegerProperty();
          longProperty.setFormat("int64");
          return longProperty;
        });
        put(ScalarType.BIGINTEGER, any -> new LongProperty());
        put(ScalarType.BIGDECIMAL, any -> new DecimalProperty());
        put(ScalarType.DOUBLE, any -> new DoubleProperty());
        put(ScalarType.FLOAT, any -> new FloatProperty());
        put(ScalarType.BOOLEAN, any -> new BooleanProperty());
        put(ScalarType.BINARY, any -> new BinaryProperty());
        put(ScalarType.DATE, any -> new DateProperty());
        put(ScalarType.DATE_TIME, any -> new DateTimeProperty());
        put(ScalarType.UUID, any -> new UUIDProperty());
      }};

  @Override
  public Property convert(ScalarModelSpecification source) {
    return SCALAR_SCHEMA_FACTORY
        .getOrDefault(source.getType(), any -> null)
        .apply(source);
  }
}
