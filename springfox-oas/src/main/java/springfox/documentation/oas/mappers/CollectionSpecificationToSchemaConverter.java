package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ByteArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import org.mapstruct.factory.Mappers;
import org.springframework.core.convert.converter.Converter;
import springfox.documentation.schema.CollectionSpecification;
import springfox.documentation.schema.CollectionType;
import springfox.documentation.schema.ScalarModelSpecification;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ModelNamesRegistry;

public class CollectionSpecificationToSchemaConverter implements Converter<CollectionSpecification, Schema<?>> {
  private final ModelNamesRegistry modelNamesRegistry;

  public CollectionSpecificationToSchemaConverter(ModelNamesRegistry modelNamesRegistry) {
    this.modelNamesRegistry = modelNamesRegistry;
  }

  @Override
    public Schema<?> convert(CollectionSpecification source) {
        ArraySchema arraySchema = new ArraySchema();
        if (source.getModel().getScalar()
            .map(ScalarModelSpecification::getType).orElse(null) == ScalarType.BYTE) {
            return new ByteArraySchema();
        } else {
            arraySchema.items(Mappers.getMapper(SchemaMapper.class).mapFrom(source.getModel(), modelNamesRegistry));
        }
        if (source.getCollectionType() == CollectionType.SET) {
            arraySchema.uniqueItems(true);
        }
        return arraySchema;
    }
}
