package springfox.documentation.schema;

public class ReferenceModelSpecification {
  private final ModelKey modelKey;

  public ReferenceModelSpecification(ModelKey modelKey) {
    this.modelKey = modelKey;
  }

  public ModelKey getKey() {
    return modelKey;
  }
}
