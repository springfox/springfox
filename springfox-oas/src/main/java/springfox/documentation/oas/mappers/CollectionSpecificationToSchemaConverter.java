package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ByteArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import org.mapstruct.factory.Mappers;
import org.springframework.core.convert.converter.Converter;
import springfox.documentation.schema.CollectionSpecification;
import springfox.documentation.schema.CollectionType;
import springfox.documentation.schema.ScalarType;

public class CollectionSpecificationToSchemaConverter implements Converter<CollectionSpecification, Schema<?>> {
    @Override
    public Schema<?> convert(CollectionSpecification source) {
        ArraySchema arraySchema = new ArraySchema();
        if (source.getModel().getScalar().isPresent()
            && source.getModel().getScalar().get().getType() == ScalarType.BYTE) {
            return new ByteArraySchema();
        } else {
            arraySchema.items(Mappers.getMapper(SchemaMapper.class).mapFrom(source.getModel()));
        }
        if (source.getCollectionType() == CollectionType.SET) {
            arraySchema.uniqueItems(true);
        }
        return arraySchema;
    }
}
