package springfox.documentation.schema;

import java.util.Objects;

public class QualifiedModelName {
  private final String namespace;
  private final String name;

  public QualifiedModelName(
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
    QualifiedModelName qualifiedModelName = (QualifiedModelName) o;
    return Objects.equals(
        namespace,
        qualifiedModelName.namespace) &&
        Objects.equals(
            name,
            qualifiedModelName.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        namespace,
        name);
  }

  @Override
  public String toString() {
    return "ModelName{" +
        "namespace='" + namespace + '\'' +
        ", name='" + name + '\'' +
        '}';
  }
}
