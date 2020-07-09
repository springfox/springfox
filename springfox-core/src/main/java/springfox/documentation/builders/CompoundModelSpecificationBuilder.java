package springfox.documentation.builders;

import org.springframework.lang.NonNull;
import springfox.documentation.schema.CompoundModelSpecification;
import springfox.documentation.schema.ModelKeyBuilder;
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
  private final Map<String, PropertySpecificationBuilder> properties = new HashMap<>();
  private final List<ReferenceModelSpecification> subclassReferences = new ArrayList<>();
  private Integer maxProperties;
  private Integer minProperties;
  private ModelKeyBuilder modelKey;
  private String discriminator;

  private PropertySpecificationBuilder propertyBuilder(String name) {
    return properties.computeIfAbsent(name, PropertySpecificationBuilder::new);
  }

  /**
   * Provides method to create a property with given name
   *
   * @param name - name of the property to create
   * @return returns a function that that provides a consumer for building a property
   */
  public Function<Consumer<PropertySpecificationBuilder>, CompoundModelSpecificationBuilder> property(
      @NonNull String name) {
    return property -> {
      property.accept(propertyBuilder(name));
      return this;
    };
  }

  /**
   * Provides method to maybe create a property with given name. If the property doesnt exist the consumer is a no-op.
   * Whatever we build downstream when the property doesnt exist is thrown away.
   *
   * @param name - name of the property to create
   * @return returns a function that that provides a consumer for building a property
   */
  public Function<Consumer<PropertySpecificationBuilder>, CompoundModelSpecificationBuilder> propertyIfExists(
      @NonNull String name) {
    return property -> {
      if (properties.containsKey(name)) {
        property.accept(propertyBuilder(name));
      } else {
        PropertySpecificationBuilder throwAwayBuilder = new PropertySpecificationBuilder(name);
        property.accept(throwAwayBuilder);
      }
      return this;
    };
  }

  /**
   * Provides a fluent builder consumer for building a model key
   *
   * @param consumer - builder consumer
   * @return this
   */
  public CompoundModelSpecificationBuilder modelKey(@NonNull Consumer<ModelKeyBuilder> consumer) {
    if (modelKey == null) {
      this.modelKey = new ModelKeyBuilder();
    }
    consumer.accept(modelKey);
    return this;
  }

  /**
   * Provides override for the max properties. It uses the number of actual properties when not provided.
   *
   * @param maxProperties - maximum properties that need to be set
   * @return this
   */
  public CompoundModelSpecificationBuilder maxProperties(Integer maxProperties) {
    this.maxProperties = maxProperties;
    return this;
  }

  /**
   * Provides override for the min properties. It uses the number of actual properties when not provided.
   *
   * @param minProperties - minimum properties that need to be set
   * @return this
   */
  public CompoundModelSpecificationBuilder minProperties(Integer minProperties) {
    this.minProperties = minProperties;
    return this;
  }

  public CompoundModelSpecification build() {
    List<PropertySpecification> properties = this.properties.values().stream()
        .map(PropertySpecificationBuilder::build)
        .filter(prop -> !prop.getHidden())
        .collect(Collectors.toList());
    if (modelKey != null) {
      return new CompoundModelSpecification(
          modelKey.build(),
          properties,
          maxProperties == null ? properties.size() : maxProperties,
          minProperties == null ? properties.size() : minProperties,
          discriminator,
          subclassReferences);
    }
    return null;
  }


  /**
   * Copies from an existing model
   *
   * @param other - other model to copy from
   * @return this
   */
  public CompoundModelSpecificationBuilder copyOf(CompoundModelSpecification other) {
    if (other == null) {
      return this;
    }
    return modelKey(m -> m.copyOf(other.getModelKey()))
        .properties(other.getProperties())
        .maxProperties(other.getMaxProperties())
        .minProperties(other.getMinProperties())
        .discriminator(other.getDiscriminator())
        .subclassReferences(other.getSubclassReferences());
  }

  /**
   * Copies existing set of properties
   *
   * @param properties - properties to copy from
   * @return this
   */
  public CompoundModelSpecificationBuilder properties(Collection<PropertySpecification> properties) {
    properties.forEach(each -> this.property(each.getName())
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
        }));
    return this;
  }

  /**
   * Inheritance discriminator
   *
   * @param discriminator - property to discriminate on
   * @return this
   */
  public CompoundModelSpecificationBuilder discriminator(String discriminator) {
    this.discriminator = discriminator;
    return this;
  }

  /**
   * References to subclasses
   *
   * @param subclassReferences - the reference specifications of subclasses
   * @return this
   */
  public CompoundModelSpecificationBuilder subclassReferences(
      Collection<ReferenceModelSpecification> subclassReferences) {
    this.subclassReferences.addAll(subclassReferences);
    return this;
  }
}