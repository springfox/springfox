package springfox.documentation.schema;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

import springfox.documentation.service.AllowableValues;

public final class CyclicModelRef implements ModelReference {
  private final String type;
  private final String typeSignature;
  private final Optional<AllowableValues> allowableValues;
  private final String modelId;

  public CyclicModelRef(String type, String typeSignature, AllowableValues allowableValues,
      String modelId) {
    this.type = type;
    this.typeSignature = typeSignature;
    this.allowableValues = Optional.fromNullable(allowableValues);
    this.modelId = modelId;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public boolean isCollection() {
    return false;
  }

  @Override
  public boolean isMap(){
    return false;
  }

  @Override
  public boolean isCyclic() {
    return true;
  }

  @Override
  public String getItemType() {
    return null;
  }

  @Override
  public AllowableValues getAllowableValues() {
    return allowableValues.orNull();
  }

  @Override
  public Optional<ModelReference> itemModel() {
    return Optional.absent();
  }

  @Override
  public Optional<String> getModelId() {
    return Optional.of(modelId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(typeSignature, allowableValues);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CyclicModelRef that = (CyclicModelRef) o;

    return Objects.equal(typeSignature, that.typeSignature) &&
        Objects.equal(allowableValues, that.allowableValues);
    }

}
