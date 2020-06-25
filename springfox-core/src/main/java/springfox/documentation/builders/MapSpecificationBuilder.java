package springfox.documentation.builders;

import springfox.documentation.schema.MapSpecification;

import java.util.function.Consumer;

public class MapSpecificationBuilder {
  private ModelSpecificationBuilder key;
  private ModelSpecificationBuilder value;

  public MapSpecificationBuilder key(Consumer<ModelSpecificationBuilder> consumer) {
    if (key == null) {
      key = new ModelSpecificationBuilder();
    }
    consumer.accept(key);
    return this;
  }

  public MapSpecificationBuilder value(Consumer<ModelSpecificationBuilder> consumer) {
    if (value == null) {
      value = new ModelSpecificationBuilder();
    }
    consumer.accept(value);
    return this;
  }

  public MapSpecificationBuilder copyOf(MapSpecification source) {
    if (source == null) {
      return this;
    }
    return this.key(k -> k.copyOf(source.getKey()))
        .value(v -> v.copyOf(source.getValue()));
  }

  public MapSpecification build() {
    if (key == null || value == null) {
      return null;
    }
    return new MapSpecification(key.build(), value.build());
  }
}