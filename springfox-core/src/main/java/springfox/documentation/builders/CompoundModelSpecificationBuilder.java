package springfox.documentation.builders;

import springfox.documentation.schema.CompoundModelSpecification;
import springfox.documentation.schema.PropertySpecification;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompoundModelSpecificationBuilder {
  private final ModelSpecificationBuilder parent;
  private Map<String, PropertySpecificationBuilder> properties = new HashMap<>();
  private Integer maxProperties;
  private Integer minProperties;

  public CompoundModelSpecificationBuilder(ModelSpecificationBuilder parent) {
    this.parent = parent;
  }

  public PropertySpecificationBuilder propertyBuilder(String name) {
    return properties.computeIfAbsent(
        name,
        n -> new PropertySpecificationBuilder(
            n,
            this));
  }

  public CompoundModelSpecificationBuilder maxProperties(Integer maxProperties) {
    this.maxProperties = maxProperties;
    return this;
  }

  public CompoundModelSpecificationBuilder minProperties(Integer minProperties) {
    this.minProperties = minProperties;
    return this;
  }

  public ModelSpecificationBuilder yield() {
    return parent;
  }

  public CompoundModelSpecification build() {
    List<PropertySpecification> properties
        = this.properties.values().stream()
                         .map(PropertySpecificationBuilder::build)
                         .collect(Collectors.toList());
    if (properties.size() > 0) {
      return new CompoundModelSpecification(
          properties,
          maxProperties,
          minProperties);
    }
    return null;
  }

  public CompoundModelSpecificationBuilder copyOf(CompoundModelSpecification other) {
    if (other == null) {
      return this;
    }
    return properties(other.getProperties())
        .maxProperties(other.getMaxProperties())
        .minProperties(other.getMinProperties());
  }

  public CompoundModelSpecificationBuilder properties(Collection<PropertySpecification> properties) {
    properties.forEach(each -> {
      PropertySpecificationBuilder propertyBuilder
          = this.propertyBuilder(each.getName())
                .type(each.getType())
                .withAllowEmptyValue(each.getAllowEmptyValue())
                .withDefaultValue(each.getDefaultValue())
                .withDeprecated(each.getDeprecated())
                .withDescription(each.getDescription())
                .withExample(each.getExample())
                .withIsHidden(each.getHidden())
                .withNullable(each.getNullable())
                .withPosition(each.getPosition())
                .withReadOnly(each.getReadOnly())
                .withRequired(each.getRequired())
                .withVendorExtensions(each.getVendorExtensions())
                .withWriteOnly(each.getWriteOnly());
      each.getFacets()
          .forEach(f -> propertyBuilder.facetBuilder(f.facetBuilder())
                                       .copyOf(f));
    });
    return this;
  }

}