package springfox.documentation.schema;

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
}
