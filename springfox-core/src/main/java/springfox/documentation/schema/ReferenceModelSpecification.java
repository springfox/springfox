package springfox.documentation.schema;

public class ReferenceModelSpecification {
  private final ModelSpecificationKey reference;

  public ReferenceModelSpecification(ModelSpecificationKey reference) {
    this.reference = reference;
  }

  public ModelSpecificationKey getReference() {
    return reference;
  }
}
