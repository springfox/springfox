package springfox.documentation.schema;

public class ScalarModelSpecification {
  private ScalarType type;

  public ScalarModelSpecification(ScalarType type) {
    this.type = type;
  }

  public ScalarType getType() {
    return type;
  }
}
