package springfox.documentation.schema;

import java.util.Objects;

public class ReferenceModelSpecification {
  private final ModelKey modelKey;

  public ReferenceModelSpecification(ModelKey modelKey) {
    this.modelKey = modelKey;
  }

  public ModelKey getKey() {
    return modelKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ReferenceModelSpecification that = (ReferenceModelSpecification) o;
    return Objects.equals(modelKey, that.modelKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(modelKey);
  }

  @Override
  public String toString() {
    return "ReferenceModelSpecification{" +
        "modelKey=" + modelKey +
        '}';
  }
}
