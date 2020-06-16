package springfox.documentation.builders;

import springfox.documentation.schema.CompoundModelSpecification;
import springfox.documentation.schema.ModelKey;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.schema.ReferenceModelSpecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompoundModelSpecificationBuilder {
  private final ModelSpecificationBuilder parent;
  private final Map<String, PropertySpecificationBuilder> properties = new HashMap<>();
  private final List<ReferenceModelSpecification> subclassReferences = new ArrayList<>();
  private Integer maxProperties;
  private Integer minProperties;
  private ModelKey modelKey;
  private String discriminator;

  public CompoundModelSpecificationBuilder(ModelSpecificationBuilder parent) {
    this.parent = parent;
  }

  private PropertySpecificationBuilder propertyBuilder(String name) {
    return properties.computeIfAbsent(
        name,
        n -> new PropertySpecificationBuilder(n, this));
  }

  public Function<Consumer<PropertySpecificationBuilder>, CompoundModelSpecificationBuilder> property(String name) {
    return property -> {
      property.accept(propertyBuilder(name));
      return this;
    };
  }

  public CompoundModelSpecificationBuilder modelKey(ModelKey modelKey) {
    this.modelKey = modelKey;
    return this;
  }

  public CompoundModelSpecificationBuilder maxProperties(Integer maxProperties) {
    this.maxProperties = maxProperties;
    return this;
  }

  public CompoundModelSpecificationBuilder minProperties(Integer minProperties) {
    this.minProperties = minProperties;
    return this;
  }

  public CompoundModelSpecification build() {
    List<PropertySpecification> properties
        = this.properties.values().stream()
                         .map(PropertySpecificationBuilder::build)
                         .collect(Collectors.toList());
    if (modelKey != null) {
      return new CompoundModelSpecification(
          modelKey,
          properties,
          maxProperties,
          minProperties,
          discriminator,
          subclassReferences);
    }
    return null;
  }

  public CompoundModelSpecificationBuilder copyOf(CompoundModelSpecification other) {
    if (other == null) {
      return this;
    }
    return modelKey(other.getModelKey())
        .properties(other.getProperties())
        .maxProperties(other.getMaxProperties())
        .minProperties(other.getMinProperties())
        .discriminator(other.getDiscriminator())
        .subclassReferences(other.getSubclassReferences());
  }

  public CompoundModelSpecificationBuilder properties(Collection<PropertySpecification> properties) {
    properties.forEach(each -> {
      this.property(each.getName())
          .apply(p -> {
            p.type(each.getType())
             .allowEmptyValue(each.getAllowEmptyValue())
             .defaultValue(each.getDefaultValue())
             .deprecated(each.getDeprecated())
             .description(each.getDescription())
             .example(each.getExample())
             .isHidden(each.getHidden())
             .nullable(each.getNullable())
             .position(each.getPosition())
             .readOnly(each.getReadOnly())
             .required(each.getRequired())
             .vendorExtensions(each.getVendorExtensions())
             .xml(each.getXml())
             .writeOnly(each.getWriteOnly());
            each.getFacets()
                .forEach(f -> p.facetBuilder(f.facetBuilder())
                               .copyOf(f));
          });
    });
    return this;
  }

  public CompoundModelSpecificationBuilder discriminator(String discriminator) {
    this.discriminator = discriminator;
    return this;
  }

  public CompoundModelSpecificationBuilder subclassReferences(
      Collection<ReferenceModelSpecification> subclassReferences) {
    this.subclassReferences.addAll(subclassReferences);
    return this;
  }
}