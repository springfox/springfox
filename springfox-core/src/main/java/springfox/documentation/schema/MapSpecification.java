package springfox.documentation.schema;

import java.util.Objects;
import java.util.StringJoiner;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MapSpecification that = (MapSpecification) o;
    return key.equals(that.key) &&
        value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", MapSpecification.class.getSimpleName() + "[", "]")
        .add("key=" + key)
        .add("value=" + value)
        .toString();
  }
}
