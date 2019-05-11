package springfox.documentation.schema;

import java.util.List;

public class CompoundModelSpecification {
  private final List<PropertySpecification> properties;
  private final Integer maxProperties;
  private final Integer minProperties;


  public CompoundModelSpecification(
      List<PropertySpecification> properties,
      Integer maxProperties,
      Integer minProperties) {
    this.properties = properties;
    this.maxProperties = maxProperties;
    this.minProperties = minProperties;
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
}
