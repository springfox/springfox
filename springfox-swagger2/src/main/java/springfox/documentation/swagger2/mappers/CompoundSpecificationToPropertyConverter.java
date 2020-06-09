package springfox.documentation.swagger2.mappers;

import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import org.springframework.core.convert.converter.Converter;
import springfox.documentation.schema.CompoundModelSpecification;
import springfox.documentation.service.ModelNamesRegistry;

import java.util.Objects;
import java.util.stream.Collectors;

public class CompoundSpecificationToPropertyConverter implements Converter<CompoundModelSpecification, Property> {
  private final ModelNamesRegistry modelNamesRegistry;

  public CompoundSpecificationToPropertyConverter(ModelNamesRegistry modelNamesRegistry) {
    this.modelNamesRegistry = modelNamesRegistry;
  }

  @Override
  public Property convert(CompoundModelSpecification source) {
    ObjectProperty schema = new ObjectProperty();
    schema.properties(source.getProperties().stream()
                          .map(p -> {
                            Property property = new PropertyMapper()
                                .fromModel(p.getType(), modelNamesRegistry);
                            if (property != null) {
                              property.setName(p.getName());
                              return property;
                            }
                            return null;
                          })
                          .filter(Objects::nonNull)
                          .collect(Collectors.toMap(Property::getName, s -> s)));
    return schema;
  }
}
