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
import springfox.documentation.schema.Model;
import springfox.documentation.schema.ModelReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static springfox.documentation.builders.BuilderDefaults.nullToEmptyList;
import static springfox.documentation.builders.BuilderDefaults.nullToEmptySet;

class InheritanceDeterminer {
  private final Map<String, Set<RefModel>> parentLookup = new HashMap<String, Set<RefModel>>();

  InheritanceDeterminer(Map<String, Model> models) {
    for (Model each : models.values()) {
      for (ModelReference modelReference : nullToEmptyList(each.getSubTypes())) {
        Set<RefModel> refModels = nullToEmptySet(parentLookup.get(modelReference.getType()));
        refModels.add(toRefModel(each));
        parentLookup.put(modelReference.getType(), refModels);
      }
    }
  }

  boolean hasParent(Model model) {
    return parentLookup.containsKey(model.getName());
  }

  Set<RefModel> parents(Model model) {
    return parentLookup.get(model.getName());
  }

  private RefModel toRefModel(Model model) {
    return new RefModel(model.getName());
  }
}
