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

import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("deprecation")
public class MergingContext {

  private final String rootId;
  private final String parameterId;
  private final Map<String, Set<String>> circlePath;
  private final Map<String, Set<String>> circleParameters;
  private final Map<String, ComparisonCondition> globalComparisonConditions;
  private final Map<String, Set<springfox.documentation.schema.Model>> typedModelMap;
  private final Map<String, String> modelIdToParameterId;
  private final Map<String, ModelContext> contextMap;
  private final Map<String, springfox.documentation.schema.Model> currentBranch;
  private final Set<String> seenModels;

  public MergingContext(
      String parameterId,
      Map<String, Set<springfox.documentation.schema.Model>> typedModelMap,
      Map<String, String> modelIdToParameterId,
      Map<String, springfox.documentation.schema.Model> currentBranch,
      Map<String, ModelContext> contextMap) {
    this.rootId = "";
    this.parameterId = parameterId;
    this.globalComparisonConditions = new HashMap<>();
    this.circlePath = new HashMap<>();
    this.circleParameters = new HashMap<>();
    this.contextMap = Collections.unmodifiableMap(copyMap(contextMap));
    this.currentBranch = Collections.unmodifiableMap(copyMap(currentBranch));
    this.typedModelMap = Collections.unmodifiableMap(copyMap(typedModelMap));
    this.modelIdToParameterId = Collections.unmodifiableMap(copyMap(modelIdToParameterId));
    this.seenModels = new HashSet<>();
  }

  private MergingContext(
      String rootId,
      Set<String> seenModels,
      MergingContext mergingContext) {
    this.rootId = rootId;
    this.parameterId = mergingContext.parameterId;
    this.circlePath = Collections.unmodifiableMap(copyMap(mergingContext.circlePath));
    this.circleParameters = Collections.unmodifiableMap(copyMap(mergingContext.circleParameters));
    this.globalComparisonConditions = Collections
        .unmodifiableMap(copyMap(mergingContext.globalComparisonConditions));
    this.contextMap = mergingContext.contextMap;
    this.currentBranch = mergingContext.currentBranch;
    this.typedModelMap = mergingContext.typedModelMap;
    this.modelIdToParameterId = mergingContext.modelIdToParameterId;
    this.seenModels = Collections.unmodifiableSet(seenModels);
  }

  private MergingContext(
      String rootId,
      Set<String> seenModels,
      Map<String, Set<String>> circlePath,
      Map<String, Set<String>> circleParameters,
      Map<String, ComparisonCondition> globalComparisonConditions,
      MergingContext mergingContext) {
    this.rootId = rootId;
    this.parameterId = mergingContext.parameterId;
    this.circlePath = Collections.unmodifiableMap(circlePath);
    this.circleParameters = Collections.unmodifiableMap(circleParameters);
    this.globalComparisonConditions = Collections.unmodifiableMap(globalComparisonConditions);
    this.contextMap = mergingContext.contextMap;
    this.currentBranch = mergingContext.currentBranch;
    this.typedModelMap = mergingContext.typedModelMap;
    this.modelIdToParameterId = mergingContext.modelIdToParameterId;
    this.seenModels = Collections.unmodifiableSet(seenModels);
  }

  public String getRootId() {
    return this.rootId;
  }

  public String getParameterId() {
    return this.parameterId;
  }

  public Optional<ComparisonCondition> getComparisonCondition(String modelFor) {
    return Optional.ofNullable(globalComparisonConditions.get(modelFor));
  }

  public Map<String, Set<String>> getCircles() {
    return this.circlePath;
  }

  public Set<String> getCircleParameters(String circleId) {
    return this.circleParameters.get(circleId);
  }

  public springfox.documentation.schema.Model getRootModel() {
    return this.currentBranch.get(rootId);
  }

  public boolean containsModel(String modelId) {
    return this.contextMap.containsKey(modelId);
  }

  public springfox.documentation.schema.Model getModel(String modelId) {
    return this.currentBranch.get(modelId);
  }

  public ModelContext getModelContext(String modelId) {
    return this.contextMap.get(modelId);
  }

  public String getModelParameter(String modelId) {
    return this.modelIdToParameterId.get(modelId);
  }

  public Set<springfox.documentation.schema.Model> getSimilarTypeModels(String type) {
    if (this.typedModelMap.containsKey(type)) {
      return this.typedModelMap.get(type);
    }
    return Collections.unmodifiableSet(new HashSet<>());
  }

  public boolean hasSeenBefore(String modelId) {
    return this.seenModels.contains(modelId);
  }

  public MergingContext toRootId(
      String rootId,
      Set<ComparisonCondition> comparisonConditions,
      Set<String> allowedParameters) {
    Set<String> localSeenModels = new HashSet<>(this.seenModels);
    localSeenModels.add(rootId);

    Map<String, ComparisonCondition> globalComparisonConditions = copyMap(
        this.globalComparisonConditions);
    comparisonConditions
        .forEach(condition -> globalComparisonConditions.put(condition.getModelFor(), condition));

    Map<String, Set<String>> circleParameters = copyMap(this.circleParameters);
    circleParameters.put(this.rootId, new HashSet<>(allowedParameters));

    Map<String, Set<String>> circlePath = copyMap(this.circlePath);
    circlePath.forEach((k, v) -> v.add(rootId));

    circlePath.put(this.rootId, new HashSet<>(Collections.singletonList(rootId)));

    return new MergingContext(rootId, localSeenModels, circlePath, circleParameters,
        globalComparisonConditions, this);
  }

  public MergingContext toRootId(String rootId) {
    Set<String> localSeenModels = new HashSet<>(this.seenModels);
    localSeenModels.add(rootId);
    return new MergingContext(rootId, localSeenModels, this);
  }

  private static <k, v> Map<k, v> copyMap(Map<k, v> originalMap) {
    Map<k, v> newMap = new HashMap<>(originalMap.size());
    newMap.putAll(originalMap);
    return newMap;
  }

}
