package springfox.documentation.oas.mappers;

import org.mapstruct.Mapper;
import springfox.documentation.schema.ModelSpecification;

import javax.xml.validation.Schema;

@Mapper
public class SchemaMapper {
  public Schema mapFrom(ModelSpecification modelSpecification) {
    return null;
  }
}
