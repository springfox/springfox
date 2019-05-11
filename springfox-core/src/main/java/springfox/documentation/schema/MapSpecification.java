package springfox.documentation.schema;

public class MapSpecification {
  private final ModelSpecification key;
  private final ModelSpecification value;


  public MapSpecification(
      ModelSpecification key,
      ModelSpecification value) {
    this.key = key;
    this.value = value;
  }

  public ModelSpecification getKey() {
    return key;
  }

  public ModelSpecification getValue() {
    return value;
  }
}
