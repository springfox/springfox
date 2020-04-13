package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.mapstruct.factory.Mappers;
import org.springframework.core.convert.converter.Converter;
import springfox.documentation.schema.MapSpecification;

public class MapSpecificationToSchemaConverter implements Converter<MapSpecification, Schema<?>> {
    @Override
    public Schema<?> convert(MapSpecification source) {
        Schema<?> schema = Mappers.getMapper(SchemaMapper.class).mapFrom(source.getValue());
        return new MapSchema()
                .additionalProperties(schema);
    }
}
