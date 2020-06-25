package springfox.documentation.builders;

import springfox.documentation.schema.ModelKeyBuilder;
import springfox.documentation.schema.ReferenceModelSpecification;

import java.util.function.Consumer;

public class ReferenceModelSpecificationBuilder {
  private ModelKeyBuilder modelKey;

  public ReferenceModelSpecificationBuilder key(Consumer<ModelKeyBuilder> consumer) {
    if (modelKey == null) {
      modelKey = new ModelKeyBuilder();
    }
    consumer.accept(modelKey);
    return this;
  }

  public ReferenceModelSpecificationBuilder copyOf(ReferenceModelSpecification source) {
    if (source == null) {
      return this;
    }
    return this.key(k -> k.copyOf(source.getKey()));
  }

  public ReferenceModelSpecification build() {
    if (modelKey == null) {
      return null;
    }
    return new ReferenceModelSpecification(modelKey.build());
  }
}