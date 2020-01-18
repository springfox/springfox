package springfox.documentation.schema;

import java.util.Objects;
import java.util.StringJoiner;

public class ModelKey {
  private final String namespace;
  private final String name;
  private final boolean isResponse;

  public ModelKey(
      String namespace,
      String name,
      boolean isResponse) {
    this.namespace = namespace;
    this.name = name;
    this.isResponse = isResponse;
  }

  public String getNamespace() {
    return namespace;
  }

  public String getName() {
    return name;
  }

  public boolean isResponse() {
    return isResponse;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModelKey modelKey = (ModelKey) o;
    return isResponse == modelKey.isResponse &&
        Objects.equals(
            namespace,
            modelKey.namespace) &&
        name.equals(modelKey.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        namespace,
        name,
        isResponse);
  }

  @Override
  public String toString() {
    return new StringJoiner(
        ", ",
        ModelKey.class.getSimpleName() + "[",
        "]")
        .add("namespace='" + namespace + "'")
        .add("name='" + name + "'")
        .add("isResponse=" + isResponse)
        .toString();
  }
}
