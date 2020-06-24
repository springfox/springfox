/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

import springfox.documentation.service.AllowableValues;

import java.util.Objects;
import java.util.Optional;

/**
 * Not required when using {@link ModelSpecification} instead
 * @deprecated @since 3.0.0
 */
@Deprecated
public class ModelRef implements ModelReference {
  private final String type;
  private final Optional<String> typeSignature;
  private final boolean isMap;
  private final Optional<ModelReference> itemModel;
  private final Optional<AllowableValues> allowableValues;
  private final Optional<String> modelId;

  public ModelRef(String type) {
    this(
        type,
        null,
        null,
        null,
        null);
  }

  public ModelRef(
      String type,
      ModelReference itemType) {
    this(
        type,
        itemType,
        false);
  }

  public ModelRef(
      String type,
      String typeSignature,
      ModelReference itemType,
      AllowableValues allowableValues,
      String modelId) {
    this(
        type,
        typeSignature,
        itemType,
        allowableValues,
        false,
        modelId);
  }

  public ModelRef(
      String type,
      AllowableValues allowableValues) {
    this(
        type,
        null,
        null,
        allowableValues,
        null);
  }

  public ModelRef(
      String type,
      ModelReference itemType,
      boolean isMap) {
    this(
        type,
        null,
        itemType,
        null,
        isMap,
        null);
  }

  public ModelRef(
      String type,
      String typeSignature,
      ModelReference itemModel,
      AllowableValues allowableValues,
      boolean isMap,
      String modelId) {
    this.type = type;
    this.typeSignature = Optional.ofNullable(typeSignature);
    this.isMap = isMap;
    this.allowableValues = Optional.ofNullable(allowableValues);
    this.itemModel = Optional.ofNullable(itemModel);
    this.modelId = Optional.ofNullable(modelId);
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
    return itemModel.map(ModelReference::getType)
        .orElse(null);
  }

  @Override
  public AllowableValues getAllowableValues() {
    return allowableValues.orElse(null);
  }

  @Override
  public Optional<ModelReference> itemModel() {
    return itemModel;
  }

  @Override
  public Optional<String> getModelId() {
    return modelId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        type,
        typeSignature,
        isMap,
        itemModel,
        allowableValues,
        modelId.isPresent());
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

    return Objects.equals(
        type,
        that.type)
        && Objects.equals(
        typeSignature,
        that.typeSignature)
        && Objects.equals(
        isMap,
        that.isMap)
        && Objects.equals(
        itemModel,
        that.itemModel)
        && Objects.equals(
        allowableValues,
        that.allowableValues)
        && Objects.equals(
        modelId,
        that.modelId);
  }
}
