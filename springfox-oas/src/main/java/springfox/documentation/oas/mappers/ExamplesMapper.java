package springfox.documentation.oas.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
          each.getMediaType().orElse(""),
          toOasExample(each));
    }
    return examples;
  }

  @Mappings({
      @Mapping(target = "$ref", ignore = true)
  })
  io.swagger.v3.oas.models.examples.Example toOasExample(Example from);
}
