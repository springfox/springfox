package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ModelKey {
  private final QualifiedModelName qualifiedModelName;
  private final ResolvedType viewDiscriminator;
  private final Set<ResolvedType> validationGroupDiscriminators = new HashSet<>();
  private final boolean isResponse;

  public ModelKey(
      QualifiedModelName qualifiedModelName,
      ResolvedType viewDiscriminator,
      Collection<ResolvedType> validationGroupDiscriminators,
      boolean isResponse) {
    this.qualifiedModelName = qualifiedModelName;
    this.viewDiscriminator = viewDiscriminator;
    this.validationGroupDiscriminators.addAll(validationGroupDiscriminators);
    this.isResponse = isResponse;
  }

  public QualifiedModelName getQualifiedModelName() {
    return qualifiedModelName;
  }

  public Optional<ResolvedType> getViewDiscriminator() {
    return Optional.ofNullable(viewDiscriminator);
  }

  public Set<ResolvedType> getValidationGroupDiscriminators() {
    return validationGroupDiscriminators;
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
        Objects.equals(qualifiedModelName, modelKey.qualifiedModelName) &&
        Objects.equals(viewDiscriminator, modelKey.viewDiscriminator) &&
        Objects.equals(validationGroupDiscriminators, modelKey.validationGroupDiscriminators);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        qualifiedModelName,
        viewDiscriminator,
        validationGroupDiscriminators,
        isResponse);
  }

  @Override
  public String toString() {
    return "ModelKey{" +
        "qualifiedModelName=" + qualifiedModelName +
        ", viewDiscriminator=" + viewDiscriminator +
        ", validationGroupDiscriminators=" + validationGroupDiscriminators +
        ", isResponse=" + isResponse +
        '}';
  }

  public ModelKey flippedResponse() {
    return new ModelKeyBuilder()
        .copyOf(this)
        .isResponse(!this.isResponse)
        .build();
  }
}
