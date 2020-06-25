package springfox.documentation.builders;

import springfox.documentation.schema.QualifiedModelName;

public class QualifiedModelNameBuilder {
  private String namespace;
  private String name;

  public QualifiedModelNameBuilder namespace(String namespace) {
    this.namespace = namespace;
    return this;
  }

  public QualifiedModelNameBuilder name(String name) {
    this.name = name;
    return this;
  }

  public QualifiedModelName build() {
    return new QualifiedModelName(namespace, name);
  }

  public QualifiedModelNameBuilder copyOf(QualifiedModelName other) {
    if (other == null) {
      return null;
    }
    return this.name(other.getName())
        .namespace(other.getNamespace());
  }
}