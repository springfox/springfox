package springfox.documentation.schema;

import springfox.documentation.service.ParameterStyle;

import java.util.List;

public class SimpleParameterSpecification {
  private final ParameterStyle style;
  private final Boolean explode;
  private final Boolean allowReserved;
  private final ModelSpecification model;
  private final List<Example> examples;

  public SimpleParameterSpecification(
      ParameterStyle style,
      Boolean explode,
      Boolean allowReserved,
      ModelSpecification model,
      List<Example> examples) {
    this.style = style;
    this.explode = explode;
    this.allowReserved = allowReserved;
    this.model = model;
    this.examples = examples;
  }

  public ParameterStyle getStyle() {
    return style;
  }

  public Boolean getExplode() {
    return explode;
  }

  public Boolean getAllowReserved() {
    return allowReserved;
  }

  public ModelSpecification getModel() {
    return model;
  }

  public List<Example> getExamples() {
    return examples;
  }
}
