package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.mapstruct.factory.Mappers;
import org.springframework.core.convert.converter.Converter;
import springfox.documentation.schema.MapSpecification;
import springfox.documentation.service.ModelNamesRegistry;

public class MapSpecificationToSchemaConverter implements Converter<MapSpecification, Schema<?>> {
  private final ModelNamesRegistry modelNamesRegistry;

  public MapSpecificationToSchemaConverter(ModelNamesRegistry modelNamesRegistry) {
    this.modelNamesRegistry = modelNamesRegistry;
  }

  @Override
  public Schema<?> convert(MapSpecification source) {
    Schema<?> schema = Mappers.getMapper(SchemaMapper.class)
        .mapFrom(source.getValue(), modelNamesRegistry);
    return new MapSchema().additionalProperties(schema);
  }
}
