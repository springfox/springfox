package springfox.documentation.schema;

import java.util.Objects;

public class ModelKey {
  private final String namespace;
  private final String name;

  public ModelKey(
      String namespace,
      String name) {
    this.namespace = namespace;
    this.name = name;
  }

  public String getNamespace() {
    return namespace;
  }

  public String getName() {
    return name;
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
    return namespace.equals(modelKey.namespace) &&
        name.equals(modelKey.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(namespace, name);
  }
}
