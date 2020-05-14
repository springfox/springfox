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
  private final ModelKey effectiveModelKey;

  public CompoundModelSpecification(
      ModelKey modelKey,
      ModelKey effectiveModelKey,
      Collection<PropertySpecification> properties,
      Integer maxProperties,
      Integer minProperties) {
    this.modelKey = modelKey;
    this.effectiveModelKey = effectiveModelKey;
    this.properties = new ArrayList<>(properties);
    this.maxProperties = maxProperties;
    this.minProperties = minProperties;
  }

  public ModelKey getModelKey() {
    return modelKey;
  }

  public List<PropertySpecification> getProperties() {
    return properties;
  }

  public Integer getMaxProperties() {
    return maxProperties;
  }

  public Integer getMinProperties() {
    return minProperties;
  }

  public ModelKey getEffectiveModelKey() {
    return effectiveModelKey;
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
    return Objects.equals(
        properties,
        that.properties) &&
        Objects.equals(
            maxProperties,
            that.maxProperties) &&
        Objects.equals(
            minProperties,
            that.minProperties) &&
        Objects.equals(modelKey, that.modelKey)
     && Objects.equals(getEffectiveModelKey(), that.getEffectiveModelKey());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        modelKey,
        effectiveModelKey,
        properties,
        maxProperties,
        minProperties);
  }

  @Override
  public String toString() {
    return "CompoundModelSpecification{" +
        "modelKey=" + modelKey +
        ", effecttiveModelKey=" + modelKey +
        ", properties=" + properties +
        ", maxProperties=" + maxProperties +
        ", minProperties=" + minProperties +
        '}';
  }
}
