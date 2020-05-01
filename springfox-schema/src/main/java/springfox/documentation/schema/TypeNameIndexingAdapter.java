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

  private static final Logger LOG = LoggerFactory.getLogger(TypeNameIndexingAdapter.class);

  private final Map<String, String> knownNames = new HashMap<>();

  @Override
  public Map<String, String> getNames() {
    return Collections.unmodifiableMap(knownNames);
  }

  @Override
  public Optional<String> getTypeName(String modelId) {
    return Optional.ofNullable(knownNames.get(modelId));
  }

  private boolean checkTypeRegistration(
      String typeName,
      String modelId) {
    if (knownNames.containsKey(modelId)) {
      if (!knownNames.get(modelId).equals(typeName)) {
        LOG.info("Rewriting type {} with model id: {} is not allowed, because it is already registered",
                 typeName,
                 modelId);
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
      String modelId) {
    if (checkTypeRegistration(
        typeName,
        modelId)) {
      return;
    }
    knownNames.put(
        modelId,
        typeName);
  }

  @Override
  public void registerUniqueType(
      String typeName,
      String modelId) {
    if (checkTypeRegistration(
        typeName,
        modelId)) {
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
        modelId,
        tempName);
  }

  @Override
  public void setEqualityFor(
      String modelIdOf,
      String modelIdTo) {
    if (!knownNames.containsKey(modelIdTo)) {
      LOG.warn(
          "Model with model id: {} was not found, because it is not registered",
          modelIdTo);
      throw new IllegalStateException("Model was not found");
    }
    if (knownNames.containsKey(modelIdOf) && !knownNames.get(modelIdOf).equals(knownNames.get(modelIdTo))) {
      LOG.warn(
          "Model with model id: {} already has equality to other model",
          modelIdTo);
      throw new IllegalStateException("Model already has equality to other model");
    }
    knownNames.put(
        modelIdOf,
        knownNames.get(modelIdTo));
  }

}
