package springfox.documentation.builders;

import springfox.documentation.schema.CollectionSpecification;
import springfox.documentation.schema.CollectionType;

import java.util.function.Consumer;

public class CollectionSpecificationBuilder {
  private ModelSpecificationBuilder model;
  private CollectionType collectionType;

  public CollectionSpecificationBuilder model(Consumer<ModelSpecificationBuilder> consumer) {
    if (model == null) {
      model = new ModelSpecificationBuilder();
    }
    consumer.accept(model);
    return this;
  }

  public CollectionSpecificationBuilder collectionType(CollectionType collectionType) {
    this.collectionType = collectionType;
    return this;
  }

  public CollectionSpecification build() {
    if (model != null) {
      return new CollectionSpecification(model.build(), collectionType);
    }
    return null;
  }

  public CollectionSpecificationBuilder copyOf(CollectionSpecification other) {
    if (other == null) {
      return this;
    }
    return this.model(m -> m.copyOf(other.getModel()))
        .collectionType(other.getCollectionType());
  }
}