package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.media.Schema;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.service.ModelNamesRegistry;

import static springfox.documentation.builders.BuilderDefaults.*;

@Mapper(componentModel = "spring")
public class SchemaMapper {
  public Schema<?> mapFrom(
      ModelSpecification modelSpecification,
      @Context ModelNamesRegistry modelNamesRegistry) {
    Schema<?> schema;
    schema = modelSpecification.getScalar()
        .map(sm -> new ScalarModelToSchemaConverter().convert(sm))
        .orElse(null);

    if (schema == null) {
      schema = modelSpecification.getCompound()
          .map(cm -> new CompoundSpecificationToSchemaConverter(modelNamesRegistry).convert(cm))
          .orElse(null);
    }

    if (schema == null) {
      schema = modelSpecification.getMap()
          .map(mm -> new MapSpecificationToSchemaConverter(modelNamesRegistry).convert(mm))
          .orElse(null);
    }

    if (schema == null) {
      schema = modelSpecification.getCollection()
          .map(cm -> new CollectionSpecificationToSchemaConverter(modelNamesRegistry).convert(cm))
          .orElse(null);
    }

    if (schema == null) {
      schema = modelSpecification.getReference()
          .filter(r -> emptyToNull(r.getKey().getQualifiedModelName().getName()) != null)
          .map(cm -> new ReferenceModelSpecificationToSchemaConverter(modelNamesRegistry).convert(cm))
          .orElse(null);
    }

    if (schema != null) {
      schema.setName(modelSpecification.getName());
    }

    return schema;
  }
}
