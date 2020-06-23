package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ModelKeyBuilder {
  private QualifiedModelName qualifiedModelName;
  private ResolvedType viewDiscriminator;
  private final Set<ResolvedType> validationGroupDiscriminators = new HashSet<>();
  private boolean isResponse;

  public ModelKeyBuilder qualifiedModelName(QualifiedModelName qualifiedModelName) {
    this.qualifiedModelName = qualifiedModelName;
    return this;
  }

  public ModelKeyBuilder viewDiscriminator(ResolvedType viewDiscriminator) {
    this.viewDiscriminator = viewDiscriminator;
    return this;
  }

  public ModelKeyBuilder validationGroupDiscriminators(Collection<ResolvedType> validationGroupDiscriminators) {
    this.validationGroupDiscriminators.addAll(validationGroupDiscriminators);
    return this;
  }

  public ModelKeyBuilder isResponse(boolean isResponse) {
    this.isResponse = isResponse;
    return this;
  }

  public ModelKey build() {
    return new ModelKey(
        qualifiedModelName,
        viewDiscriminator,
        validationGroupDiscriminators,
        isResponse);
  }

  public ModelKeyBuilder copyOf(ModelKey other) {
    return this.qualifiedModelName(other.getQualifiedModelName())
        .viewDiscriminator(other.getViewDiscriminator().orElse(null))
        .validationGroupDiscriminators(other.getValidationGroupDiscriminators())
        .isResponse(other.isResponse());
  }
}