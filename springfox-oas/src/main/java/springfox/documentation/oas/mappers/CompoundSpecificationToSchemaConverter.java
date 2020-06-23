package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.mapstruct.factory.Mappers;
import org.springframework.core.convert.converter.Converter;
import springfox.documentation.schema.CompoundModelSpecification;
import springfox.documentation.service.ModelNamesRegistry;

import java.util.Objects;
import java.util.stream.Collectors;

public class CompoundSpecificationToSchemaConverter implements Converter<CompoundModelSpecification, Schema<?>> {
  private final ModelNamesRegistry modelNamesRegistry;

  public CompoundSpecificationToSchemaConverter(ModelNamesRegistry modelNamesRegistry) {
    this.modelNamesRegistry = modelNamesRegistry;
  }

  @Override
  public Schema<?> convert(CompoundModelSpecification source) {
    ObjectSchema schema = new ObjectSchema();
    schema.properties(source.getProperties().stream()
                          .map(p -> {
                            Schema<?> property = Mappers.getMapper(SchemaMapper.class)
                                .mapFrom(p.getType(), modelNamesRegistry);
                            if (property != null) {
                              return property.name(p.getName());
                            }
                            return null;
                          })
                          .filter(Objects::nonNull)
                          .collect(Collectors.toMap(Schema::getName, s -> s)));
    return schema;
  }
}
