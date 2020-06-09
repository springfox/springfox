package springfox.documentation.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CompoundModelSpecification {
  private final ModelKey modelKey;
  private final List<PropertySpecification> properties;
  private final Integer maxProperties;
  private final Integer minProperties;
  private final String discriminator;
  private final List<ReferenceModelSpecification> subclassReferences;

  public CompoundModelSpecification(
      ModelKey modelKey,
      Collection<PropertySpecification> properties,
      Integer maxProperties,
      Integer minProperties,
      String discriminator,
      List<ReferenceModelSpecification> subclassReferences) {
    this.modelKey = modelKey;
    this.properties = new ArrayList<>(properties);
    this.maxProperties = maxProperties;
    this.minProperties = minProperties;
    this.discriminator = discriminator;
    this.subclassReferences = subclassReferences;
  }

  public ModelKey getModelKey() {
    return modelKey;
  }

  public Collection<PropertySpecification> getProperties() {
    return properties;
  }

  public Integer getMaxProperties() {
    return maxProperties;
  }

  public Integer getMinProperties() {
    return minProperties;
  }

  public Collection<ReferenceModelSpecification> getSubclassReferences() {
    return subclassReferences;
  }

  public String getDiscriminator() {
    return discriminator;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompoundModelSpecification that = (CompoundModelSpecification) o;
    return Objects.equals(modelKey, that.modelKey) &&
        Objects.equals(properties, that.properties) &&
        Objects.equals(maxProperties, that.maxProperties) &&
        Objects.equals(minProperties, that.minProperties) &&
        Objects.equals(discriminator, that.discriminator) &&
        Objects.equals(subclassReferences, that.subclassReferences);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        modelKey,
        properties,
        maxProperties,
        minProperties,
        discriminator,
        subclassReferences);
  }

  @Override
  public String toString() {
    return "CompoundModelSpecification{" +
        "modelKey=" + modelKey +
        ", properties=" + properties +
        ", maxProperties=" + maxProperties +
        ", minProperties=" + minProperties +
        ", discriminator='" + discriminator + '\'' +
        ", subclassReferences=" + subclassReferences +
        '}';
  }
}
