/*
 *
 *  Copyright 2019 the original author or authors.
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
package springfox.documentation.spring.web.scanners;

import springfox.documentation.schema.Model;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MergingContext {

  private final String rootId;
  private final Map<String, Set<String>> parametersMatching;
  private final Set<String> dependencies;
  private final Set<ComparisonCondition> comparisonConditions;
  private final Map<String, Set<Model>> typedModelMap;
  private final Map<String, String> modelIdToParameterId;
  private final Map<String, ModelContext> contextMap;
  private final Map<String, Model> currentBranch;
  private final Set<String> seenModels;

  public MergingContext(
      String rootId,
      Map<String, Set<Model>> typedModelMap,
      Map<String, String> modelIdToParameterId,
      Map<String, Model> currentBranch,
      Map<String, ModelContext> contextMap) {
    this.rootId = rootId;
    this.parametersMatching = new HashMap<>();
    this.dependencies = new HashSet<>();
    this.comparisonConditions = new HashSet<>();
    this.contextMap = Collections.unmodifiableMap(new HashMap<>(contextMap));
    this.currentBranch = Collections.unmodifiableMap(new HashMap<>(currentBranch));
    this.typedModelMap = Collections.unmodifiableMap(new HashMap<>(typedModelMap));
    this.modelIdToParameterId = Collections.unmodifiableMap(new HashMap<>(modelIdToParameterId));
    this.seenModels = new HashSet<>();
  }

  //CHECKSTYLE:OFF
  private MergingContext(String rootId, Map<String, Set<String>> parametersMatching, Set<String> dependencies,
      Set<ComparisonCondition> comparisonConditions, Map<String, Set<Model>> typedModelMap,
      Map<String, String> modelIdToParameterId, Map<String, Model> currentBranch, Map<String, ModelContext> contextMap,
      Set<String> seenModels) {
    this.rootId = rootId;
    this.parametersMatching = Collections.unmodifiableMap(new HashMap<>(parametersMatching));
    this.dependencies = Collections.unmodifiableSet(new HashSet<>(dependencies));
    this.comparisonConditions = Collections.unmodifiableSet(new HashSet<>(comparisonConditions));
    this.typedModelMap = Collections.unmodifiableMap(new HashMap<>(typedModelMap));
    this.modelIdToParameterId = Collections.unmodifiableMap(new HashMap<>(modelIdToParameterId));
    this.contextMap = Collections.unmodifiableMap(new HashMap<>(contextMap));
    this.currentBranch = Collections.unmodifiableMap(new HashMap<>(currentBranch));
    this.seenModels = Collections.unmodifiableSet(new HashSet<>(seenModels));
  }
  //CHECKSTYLE:ON

  public String getRootId() {
    return rootId;
  }

  public Map<String, Set<String>> getParametersMatching() {
    return parametersMatching;
  }

  public Map<String, Set<Model>> getTypedModelMap() {
    return typedModelMap;
  }

  public Map<String, String> getModelIdToParameterId() {
    return modelIdToParameterId;
  }

  public Set<String> getDependencies() {
    return dependencies;
  }

  public Set<ComparisonCondition> getComparisonConditions() {
    return comparisonConditions;
  }

  public Model getRootModel() {
    return this.currentBranch.get(rootId);
  }

  public boolean containsModel(String modelId) {
    return this.contextMap.containsKey(modelId);
  }

  public Model getModel(String modelId) {
    return this.currentBranch.get(modelId);
  }

  public ModelContext getModelContext(String modelId) {
    return this.contextMap.get(modelId);
  }

  public String getModelParameter(String modelId) {
    return this.modelIdToParameterId.get(modelId);
  }

  public Set<Model> getSimilarTypeModels(String type) {
    if (this.typedModelMap.containsKey(type)) {
      return this.typedModelMap.get(type);
    }
    return Collections.unmodifiableSet(new HashSet<>());
  }

  public boolean hasSeenBefore(String modelId) {
    return this.seenModels.contains(modelId);
  }

  public MergingContext populateDependencies(Map<String, Set<String>> parametersMatching, Set<String> dependencies,
      Set<ComparisonCondition> comparisonConditions) {
    dependencies.remove(rootId);
    return new MergingContext(this.rootId, parametersMatching, dependencies, comparisonConditions, this.typedModelMap,
        this.modelIdToParameterId, this.currentBranch, this.contextMap, this.seenModels);
  }

  public MergingContext toRootId(String rootId) {
    Set<String> localSeenModels = new HashSet<>(this.seenModels);
    localSeenModels.add(rootId);
    return new MergingContext(
        rootId,
        new HashMap<>(),
        new HashSet<>(),
        new HashSet<>(),
        this.typedModelMap,
        this.modelIdToParameterId,
        this.currentBranch,
        this.contextMap,
        localSeenModels);
  }

  public MergingContext withNewBranch(
      Map<String, Model> currentBranch,
      Map<String, ModelContext> contextMap) {
    return new MergingContext(
        "",
        this.typedModelMap,
        this.modelIdToParameterId,
        currentBranch,
        contextMap);
  }

}
