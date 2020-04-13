package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.media.Schema;
import org.mapstruct.Mapper;
import springfox.documentation.schema.ModelSpecification;

@Mapper(componentModel = "spring")
public class SchemaMapper {
  public Schema mapFrom(ModelSpecification modelSpecification) {
    return null;
  }
}
