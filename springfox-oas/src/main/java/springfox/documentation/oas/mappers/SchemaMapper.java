package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.media.Schema;
import org.mapstruct.Mapper;
import springfox.documentation.schema.ModelSpecification;

@Mapper(componentModel = "spring")
public class SchemaMapper {
    public Schema<?> mapFrom(ModelSpecification modelSpecification) {
        Schema<?> schema;
        schema = modelSpecification.getScalar()
                .map(sm -> new ScalarModelToSchemaConverter().convert(sm))
                .orElse(null);

        if (schema == null) {
            schema = modelSpecification.getCompound()
                    .map(cm -> new CompoundSpecificationToSchemaConverter().convert(cm))
                    .orElse(null);
        }

        if (schema == null) {
            schema = modelSpecification.getMap()
                    .map(mm -> new MapSpecificationToSchemaConverter().convert(mm))
                    .orElse(null);
        }

        if (schema == null) {
            schema = modelSpecification.getCollection()
                    .map(cm -> new CollectionSpecificationToSchemaConverter().convert(cm))
                    .orElse(null);
        }

        if (schema == null) {
            schema = modelSpecification.getReference()
                    .map(cm -> new ReferenceModelSpecificationToSchemaConverter().convert(cm))
                    .orElse(null);
        }

        if (schema != null) {
          schema.setName(modelSpecification.getName());
        }
        
        return schema;
    }
}
