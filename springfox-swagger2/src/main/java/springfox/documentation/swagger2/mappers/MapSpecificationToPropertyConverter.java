package springfox.documentation.swagger2.mappers;

import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import org.springframework.core.convert.converter.Converter;
import springfox.documentation.schema.MapSpecification;
import springfox.documentation.service.ModelNamesRegistry;

public class MapSpecificationToPropertyConverter implements Converter<MapSpecification, Property> {
  private final ModelNamesRegistry modelNamesRegistry;

  public MapSpecificationToPropertyConverter(ModelNamesRegistry modelNamesRegistry) {
    this.modelNamesRegistry = modelNamesRegistry;
  }

  @Override
  public Property convert(MapSpecification source) {
    Property schema = new PropertyMapper()
        .fromModel(source.getValue(), modelNamesRegistry);
    return new MapProperty()
        .additionalProperties(schema);
  }
}
