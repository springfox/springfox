package springfox.documentation.swagger2.mappers;

import io.swagger.models.Model;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import springfox.documentation.service.ApiListing;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Mapper(componentModel = "spring")
public abstract class CompatibilityModelMapper {
  @Autowired
  @Value("${springfox.documentation.swagger.v2.use-model-v3:true}")
  private boolean useModelV3;

  @SuppressWarnings("deprecation")
  Map<String, Model> modelsFromApiListings(Map<String, List<ApiListing>> apiListings) {
    if (useModelV3) {
      return Mappers.getMapper(ModelSpecificationMapper.class).modelsFromApiListings(apiListings);
    } else {
      Map<String, springfox.documentation.schema.Model> definitions = new TreeMap<>();
      apiListings.values().stream()
          .flatMap(Collection::stream)
          .forEachOrdered(each -> definitions.putAll(each.getModels()));
      return Mappers.getMapper(ModelMapper.class).mapModels(definitions);
    }
  }
}
