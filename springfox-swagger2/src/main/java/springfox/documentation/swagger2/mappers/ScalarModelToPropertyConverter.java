package springfox.documentation.swagger2.mappers;

import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.ByteArrayProperty;
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.DateTimeProperty;
import io.swagger.models.properties.DecimalProperty;
import io.swagger.models.properties.DoubleProperty;
import io.swagger.models.properties.FileProperty;
import io.swagger.models.properties.FloatProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.LongProperty;
import io.swagger.models.properties.ObjectProperty;
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
  private static final Map<ScalarType, Function<ScalarModelSpecification, Property>>
      SCALAR_SCHEMA_FACTORY = new HashMap<>();

  static {
    SCALAR_SCHEMA_FACTORY.put(ScalarType.STRING, any -> new StringProperty());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.BYTE, any -> new ByteArrayProperty());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.INTEGER, any -> new IntegerProperty());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.LONG, any -> {
      IntegerProperty longProperty = new IntegerProperty();
      longProperty.setFormat("int64");
      return longProperty;
    });
    SCALAR_SCHEMA_FACTORY.put(ScalarType.BIGINTEGER, any -> new LongProperty());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.BIGDECIMAL, any -> new DecimalProperty());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.DOUBLE, any -> new DoubleProperty());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.FLOAT, any -> new FloatProperty());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.BOOLEAN, any -> new BooleanProperty());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.BINARY, any -> new FileProperty());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.DATE, any -> new DateProperty());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.DATE_TIME, any -> new DateTimeProperty());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.UUID, any -> new UUIDProperty());
    SCALAR_SCHEMA_FACTORY.put(ScalarType.URI, any -> new StringProperty(StringProperty.Format.URI));
    SCALAR_SCHEMA_FACTORY.put(ScalarType.URL, any -> new StringProperty(StringProperty.Format.URL));
    SCALAR_SCHEMA_FACTORY.put(ScalarType.OBJECT, any -> new ObjectProperty());
  }

  @Override
  public Property convert(ScalarModelSpecification source) {
    return SCALAR_SCHEMA_FACTORY
        .getOrDefault(source.getType(), any -> null)
        .apply(source);
  }
}
