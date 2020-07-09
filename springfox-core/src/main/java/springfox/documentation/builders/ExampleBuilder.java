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

  public ExampleBuilder mediaType(String mediaType) {
    this.mediaType = emptyToNull(mediaType);
    return this;
  }

  /**
   * @see ExampleBuilder#id(String)
   * @deprecated @since 3.0.0
   * @param id - id
   * @return ExampleBuilder
   */
  @Deprecated
  public ExampleBuilder withId(String id) {
    this.id = id;
    return this;
  }

  /**
   * @see ExampleBuilder#summary(String)
   * @deprecated @since 3.0.0
   * @param summary - summary
   * @return ExampleBuilder
   */
  @Deprecated
  public ExampleBuilder withSummary(String summary) {
    this.summary = summary;
    return this;
  }

  /**
   * @see ExampleBuilder#description(String)
   * @deprecated @since 3.0.0
   * @param description - description
   * @return ExampleBuilder
   */
  @Deprecated
  public ExampleBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  /**
   * @see ExampleBuilder#externalValue(String)
   * @deprecated @since 3.0.0
   * @param externalValue - externalValue
   * @return ExampleBuilder
   */
  @Deprecated
  public ExampleBuilder withExternalValue(String externalValue) {
    this.externalValue = externalValue;
    return this;
  }

  /**
   * @see ExampleBuilder#id(String)
   * @since 3.0
   * @param id - id
   * @return ExampleBuilder
   */
  public ExampleBuilder id(String id) {
    this.id = id;
    return this;
  }

  /**
   * @see ExampleBuilder#summary(String)
   * @since 3.0
   * @param summary - externalValue
   * @return ExampleBuilder
   */
  public ExampleBuilder summary(String summary) {
    this.summary = summary;
    return this;
  }

  /**
   * @see ExampleBuilder#description(String)
   * @since 3.0
   * @param description - description
   * @return ExampleBuilder
   */
  public ExampleBuilder description(String description) {
    this.description = description;
    return this;
  }

  /**
   * @see ExampleBuilder#externalValue(String)
   * @since 3.0
   * @param externalValue - externalValue
   * @return ExampleBuilder
   */
  public ExampleBuilder externalValue(String externalValue) {
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
        emptyToNull(mediaType));
  }
}