package springfox.documentation.oas.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import springfox.documentation.schema.Example;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

@Mapper(componentModel = "spring", uses = VendorExtensionsMapper.class)
public interface ExamplesMapper {
  default Map<String, io.swagger.v3.oas.models.examples.Example> mapExamples(Collection<Example> from) {
    Map<String, io.swagger.v3.oas.models.examples.Example> examples = new TreeMap<>();
    for (Example each : from) {
      examples.put(
          each.getId(),
          toOasExample(each));
    }
    return examples;
  }

  @AfterMapping
  default void afterMappingParameter(
      Example from,
      @MappingTarget io.swagger.v3.oas.models.examples.Example target) {
    target.extensions(new VendorExtensionsMapper().mapExtensions(from.getExtensions()));
  }

  @Mappings({
      @Mapping(target = "$ref", ignore = true),
      @Mapping(target = "extensions", ignore = true)
  })
  io.swagger.v3.oas.models.examples.Example toOasExample(Example from);
}
