/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
package springfox.documentation.swagger2.mappers;

import io.swagger.models.RefModel;
import java.util.HashMap;
import java.util.Map;

import static springfox.documentation.builders.BuilderDefaults.*;

/**
 * Not needed when using {@link ModelSpecificationMapper} instead
 *
 * @deprecated @since 3.0.0
 */
@Deprecated
class InheritanceDeterminer {
  private final Map<String, RefModel> parentLookup = new HashMap<String, RefModel>();

  InheritanceDeterminer(Map<String, springfox.documentation.schema.Model> models) {
    for (springfox.documentation.schema.Model each : models.values()) {
      for (springfox.documentation.schema.ModelReference modelReference : nullToEmptyList(each.getSubTypes())) {
        parentLookup.put(modelReference.getType(), toRefModel(each));
      }
    }
  }

  boolean hasParent(springfox.documentation.schema.Model model) {
    return parentLookup.containsKey(model.getName());
  }

  RefModel parent(springfox.documentation.schema.Model model) {
    return parentLookup.get(model.getName());
  }

  private RefModel toRefModel(springfox.documentation.schema.Model model) {
    return new RefModel(model.getName());
  }
}
