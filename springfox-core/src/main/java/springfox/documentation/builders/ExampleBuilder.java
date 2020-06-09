package springfox.documentation.builders;

import springfox.documentation.schema.Example;

import static springfox.documentation.builders.BuilderDefaults.*;

public class ExampleBuilder {
  private Object value;
  private String mediaType;
  private String id;
  private String summary;
  private String description;
  private String externalValue;

  public ExampleBuilder value(Object value) {
    this.value = value;
    return this;
  }

  public ExampleBuilder withMediaType(String mediaType) {
    this.mediaType = emptyToNull(mediaType);
    return this;
  }

  public ExampleBuilder withId(String id) {
    this.id = id;
    return this;
  }

  public ExampleBuilder withSummary(String summary) {
    this.summary = summary;
    return this;
  }

  public ExampleBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public ExampleBuilder withExternalValue(String externalValue) {
    this.externalValue = externalValue;
    return this;
  }

  public Example build() {
    return new Example(
        id,
        summary,
        description,
        value,
        externalValue,
        mediaType);
  }
}