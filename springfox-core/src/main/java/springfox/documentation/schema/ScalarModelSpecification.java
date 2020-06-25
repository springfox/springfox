package springfox.documentation.schema;

import java.util.Objects;
import java.util.StringJoiner;

public class ScalarModelSpecification {
  private final ScalarType type;

  public ScalarModelSpecification(ScalarType type) {
    this.type = type;
  }

  public ScalarType getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ScalarModelSpecification that = (ScalarModelSpecification) o;
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ScalarModelSpecification.class.getSimpleName() + "[", "]")
        .add("type=" + type)
        .toString();
  }
}
