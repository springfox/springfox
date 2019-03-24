/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.schema;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import springfox.documentation.service.AllowableValues;

public class ModelRef implements ModelReference {
  private final String type;
  private final Optional<String> typeSignature;
  private final boolean isMap;
  private final Optional<ModelReference> itemModel;
  private final Optional<AllowableValues> allowableValues;
  private final Optional<String> modelId;

  public ModelRef(String type) {
    this(type, null, null, null, null);
  }

  public ModelRef(String type, ModelReference itemType) {
    this(type, itemType, false);
  }

  public ModelRef(String type, String typeSignature, ModelReference itemType, AllowableValues allowableValues,
      String modelId) {
    this(type, typeSignature, itemType, allowableValues, false, modelId);
  }

  public ModelRef(String type, AllowableValues allowableValues) {
    this(type, null, null, allowableValues, null);
  }

  public ModelRef(String type, ModelReference itemType, boolean isMap) {
    this(type, null, itemType, null, isMap, null);
  }

  public ModelRef(String type, String typeSignature, ModelReference itemModel, AllowableValues allowableValues,
      boolean isMap, String modelId) {
    this.type = type;
    this.typeSignature = Optional.fromNullable(typeSignature);
    this.isMap = isMap;
    this.allowableValues = Optional.fromNullable(allowableValues);
    this.itemModel = Optional.fromNullable(itemModel);
    this.modelId = Optional.fromNullable(modelId);
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public Optional<String> getTypeSignature() {
    return typeSignature;
  }

  @Override
  public boolean isCollection() {
    return itemModel.isPresent() && !isMap;
  }

  @Override
  public boolean isMap() {
    return itemModel.isPresent() && isMap;
  }

  @Override
  public String getItemType() {
    return itemModel.transform(toName()).orNull();
  }

  @Override
  public AllowableValues getAllowableValues() {
    return allowableValues.orNull();
  }

  @Override
  public Optional<ModelReference> itemModel() {
    return itemModel;
  }

  @Override
  public Optional<String> getModelId() {
    return modelId;
  }

  private Function<? super ModelReference, String> toName() {
    return new Function<ModelReference, String>() {
      @Override
      public String apply(ModelReference input) {
        return input.getType();
      }
    };
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(type, typeSignature, isMap, itemModel, allowableValues, modelId.isPresent());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ModelRef that = (ModelRef) o;

    return Objects.equal(type, that.type) && Objects.equal(typeSignature, that.typeSignature)
        && Objects.equal(isMap, that.isMap) && Objects.equal(itemModel, that.itemModel)
        && Objects.equal(allowableValues, that.allowableValues) && modelId.isPresent() == that.modelId.isPresent();
  }

}
