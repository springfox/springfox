package springfox.documentation.schema;

import java.util.Objects;

public class CollectionSpecification {
  private final ModelSpecification model;
  private final CollectionType collectionType;

  public CollectionSpecification(
      ModelSpecification model,
      CollectionType collectionType) {
    this.model = model;
    this.collectionType = collectionType;
  }

  public ModelSpecification getModel() {
    return model;
  }

  public CollectionType getCollectionType() {
    return collectionType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CollectionSpecification that = (CollectionSpecification) o;
    return Objects.equals(
        model,
        that.model) &&
        collectionType == that.collectionType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        model,
        collectionType);
  }

  @Override
  public String toString() {
    return "CollectionSpecification{" +
        "model=" + model +
        ", collectionType=" + collectionType +
        '}';
  }
}
