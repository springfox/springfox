package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class ModelKey {
  private final String namespace;
  private final String name;
  private final ResolvedType viewDiscriminator;
  private final ArrayList<ResolvedType> validationGroupDiscriminators = new ArrayList<>();
  private final boolean isResponse;

  public ModelKey(
      String namespace,
      String name,
      ResolvedType viewDiscriminator,
      Collection<ResolvedType> validationGroupDiscriminators,
      boolean isResponse) {
    this.namespace = namespace;
    this.name = name;
    this.viewDiscriminator = viewDiscriminator;
    this.validationGroupDiscriminators.addAll(validationGroupDiscriminators);
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
        Objects.equals(namespace, modelKey.namespace) &&
        Objects.equals(viewDiscriminator, modelKey.viewDiscriminator) &&
        Objects.equals(validationGroupDiscriminators, modelKey.validationGroupDiscriminators) &&
        name.equals(modelKey.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        namespace,
        name,
        viewDiscriminator,
        validationGroupDiscriminators,
        isResponse);
  }

  @Override
  public String toString() {
    return "ModelKey{" +
        "namespace='" + namespace + '\'' +
        ", name='" + name + '\'' +
        ", viewDiscriminator=" + viewDiscriminator +
        ", validationGroupDiscriminators=" + validationGroupDiscriminators +
        ", isResponse=" + isResponse +
        '}';
  }
}
