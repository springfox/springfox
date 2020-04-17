package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.core.convert.converter.Converter;
import springfox.documentation.schema.ReferenceModelSpecification;

public class ReferenceModelSpecificationToSchemaConverter implements Converter<ReferenceModelSpecification, Schema<?>> {
    @Override
    public Schema<?> convert(ReferenceModelSpecification source) {
        ObjectSchema objectSchema = new ObjectSchema();
        if (!source.getKey().getName().equals(Object.class.getSimpleName()) &&
                !source.getKey().getNamespace().equals(Object.class.getPackage().getName())) {
            return objectSchema.$ref(source.getKey().getName());
        }
        return objectSchema;
    }
}
