package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import springfox.documentation.builders.QualifiedModelNameBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ModelKeyBuilder {
  private final QualifiedModelNameBuilder qualifiedModelName = new QualifiedModelNameBuilder();
  private final Set<ResolvedType> validationGroupDiscriminators = new HashSet<>();
  private ResolvedType viewDiscriminator;
  private boolean isResponse;

  public ModelKeyBuilder qualifiedModelName(QualifiedModelName qualifiedModelName) {
    this.qualifiedModelName.copyOf(qualifiedModelName);
    return this;
  }

  public ModelKeyBuilder qualifiedModelName(Consumer<QualifiedModelNameBuilder> consumer) {
    consumer.accept(qualifiedModelName);
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
        qualifiedModelName.build(),
        viewDiscriminator,
        validationGroupDiscriminators,
        isResponse);
  }

  public ModelKeyBuilder copyOf(ModelKey other) {
    if (other == null) {
      return this;
    }
    return this.qualifiedModelName(other.getQualifiedModelName())
        .viewDiscriminator(other.getViewDiscriminator().orElse(null))
        .validationGroupDiscriminators(other.getValidationGroupDiscriminators())
        .isResponse(other.isResponse());
  }
}