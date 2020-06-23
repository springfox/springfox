/*
 *
 *  Copyright 2017 the original author or authors.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.spi.schema.UniqueTypeNameAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TypeNameIndexingAdapter implements UniqueTypeNameAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(TypeNameIndexingAdapter.class);

  private final Map<String, String> knownNames = new HashMap<>();

  @Override
  public Map<String, String> getNames() {
    return Collections.unmodifiableMap(knownNames);
  }

  @Override
  public Optional<String> getTypeName(String typeId) {
    return Optional.ofNullable(knownNames.get(typeId));
  }

  private boolean checkTypeRegistration(
      String typeName,
      String typeId) {
    if (knownNames.containsKey(typeId)) {
      if (!knownNames.get(typeId).equals(typeName)) {
        LOGGER.debug("Rewriting type {} with model id: {} is not allowed, because it is already registered",
                 typeName,
                 typeId);
        throw new IllegalStateException("Model already registered with different name.");
      } else {
        return true;
      }
    }

    return false;
  }

  @Override
  public void registerType(
      String typeName,
      String typeId) {
    if (checkTypeRegistration(
        typeName,
        typeId)) {
      return;
    }
    knownNames.put(
        typeId,
        typeName);
  }

  @Override
  public void registerUniqueType(
      String typeName,
      String typeId) {
    if (checkTypeRegistration(
        typeName,
        typeId)) {
      return;
    }
    Integer nameIndex = 0;
    String tempName = typeName;
    while (knownNames.values().contains(tempName)) {
      ++nameIndex;
      tempName = new StringBuilder(typeName)
          .append("_")
          .append(nameIndex).
          toString();
    }
    knownNames.put(
        typeId,
        tempName);
  }

  @Override
  public void setEqualityFor(
      String typeIdOf,
      String typeIdTo) {
    if (!knownNames.containsKey(typeIdTo)) {
      LOGGER.warn(
          "Model with model id: {} was not found, because it is not registered",
          typeIdTo);
      throw new IllegalStateException("Model was not found");
    }
    if (knownNames.containsKey(typeIdOf) && !knownNames.get(typeIdOf).equals(knownNames.get(typeIdTo))) {
      LOGGER.warn(
          "Model with model id: {} already has equality to other model",
          typeIdTo);
      throw new IllegalStateException("Model already has equality to other model");
    }
    knownNames.put(
        typeIdOf,
        knownNames.get(typeIdTo));
  }

}
