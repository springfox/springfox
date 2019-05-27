package springfox.documentation.schema;

import springfox.documentation.service.ParameterStyle;

import java.util.List;

public class SimpleParameterSpecificationBuilder {
  private ParameterStyle style;
  private Boolean explode;
  private Boolean allowReserved;
  private ModelSpecification model;
  private List<Example> examples;

  public SimpleParameterSpecificationBuilder withStyle(ParameterStyle style) {
    this.style = style;
    return this;
  }

  public SimpleParameterSpecificationBuilder withExplode(Boolean explode) {
    this.explode = explode;
    return this;
  }

  public SimpleParameterSpecificationBuilder withAllowReserved(Boolean allowReserved) {
    this.allowReserved = allowReserved;
    return this;
  }

  public SimpleParameterSpecificationBuilder withModel(ModelSpecification model) {
    this.model = model;
    return this;
  }

  public SimpleParameterSpecificationBuilder withExamples(List<Example> examples) {
    this.examples = examples;
    return this;
  }

  public SimpleParameterSpecification build() {
    return new SimpleParameterSpecification(
        style,
        explode,
        allowReserved,
        model,
        examples);
  }
}