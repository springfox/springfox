package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.core.convert.converter.Converter;
import springfox.documentation.schema.QualifiedModelName;
import springfox.documentation.schema.ReferenceModelSpecification;
import springfox.documentation.service.ModelNamesRegistry;

public class ReferenceModelSpecificationToSchemaConverter implements Converter<ReferenceModelSpecification, Schema<?>> {
  private final ModelNamesRegistry modelNamesRegistry;

  public ReferenceModelSpecificationToSchemaConverter(ModelNamesRegistry modelNamesRegistry) {
    this.modelNamesRegistry = modelNamesRegistry;
  }

  @Override
  public Schema<?> convert(ReferenceModelSpecification source) {
    ObjectSchema objectSchema = new ObjectSchema();

    QualifiedModelName qualifiedModelName = source.getKey().getQualifiedModelName();
    if ("java.lang".equals(qualifiedModelName.getNamespace())
        && "object".equals(qualifiedModelName.getName())) {
      return objectSchema;
    }
    return objectSchema
        .type(null)
        .$ref(modelNamesRegistry.nameByKey(source.getKey())
            .orElse("Error-" + qualifiedModelName));
  }
}
