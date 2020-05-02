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

package springfox.documentation.spring.web.scanners;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import springfox.documentation.builders.ModelBuilder;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.schema.Model;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.ModelProvider;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.schema.TypeNameIndexingAdapter;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.UniqueTypeNameAdapter;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static springfox.documentation.schema.ResolvedTypes.*;

@Component
public class ApiModelReader {
  private static final Logger LOG = LoggerFactory.getLogger(ApiModelReader.class);
  private final ModelProvider modelProvider;
  private final TypeResolver typeResolver;
  private final DocumentationPluginsManager pluginsManager;
  private final EnumTypeDeterminer enumTypeDeterminer;
  private final TypeNameExtractor typeNameExtractor;

  @Autowired
  public ApiModelReader(
      @Qualifier("cachedModels") ModelProvider modelProvider,
      TypeResolver typeResolver,
      DocumentationPluginsManager pluginsManager,
      EnumTypeDeterminer enumTypeDeterminer,
      TypeNameExtractor typeNameExtractor) {
    this.modelProvider = modelProvider;
    this.typeResolver = typeResolver;
    this.pluginsManager = pluginsManager;
    this.enumTypeDeterminer = enumTypeDeterminer;
    this.typeNameExtractor = typeNameExtractor;
  }

  @SuppressWarnings("rawtypes")
  public Map<String, Set<Model>> read(RequestMappingContext context) {
    Map<String, Set<Model>> mergedModelMap = new TreeMap<>();

    Map<String, Model> uniqueModels = new HashMap<>();
    Map<String, String> parameterModelMap = new HashMap<>();

    final UniqueTypeNameAdapter adapter = new TypeNameIndexingAdapter();

    Set<Class> ignorableTypes = context.getIgnorableParameterTypes();
    Set<ModelContext> modelContexts = pluginsManager.modelContexts(context);

    for (Map.Entry<String, Set<Model>> entry : context.getModelMap().entrySet()) {
      entry.getValue()
          .stream()
          .peek(model -> adapter.registerType(model.getName(), model.getId()))
          .filter(model -> !uniqueModels.containsKey(model.getName()))
          .forEach(model -> {
            uniqueModels.put(model.getName(), model);
            parameterModelMap.put(model.getId(), entry.getKey());
          });
    }

    for (ModelContext rootContext : modelContexts) {
      Map<String, Model> modelBranch = new HashMap<>();
      final Map<String, ModelContext> contextMap = new HashMap<>();
      markIgnorablesAsHasSeen(typeResolver, ignorableTypes, rootContext);
      Optional<Model> pModel = modelProvider.modelFor(rootContext);
      List<String> branchRoots = new ArrayList<>();
      if (pModel.isPresent()) {
        LOG.debug("Generated parameter model id: {}, name: {}",
            // TODO: resolve this, schema: {} models",
            pModel.get().getId(),
            pModel.get().getName());
        modelBranch.put(pModel.get().getId(), pModel.get());
        contextMap.put(pModel.get().getId(), rootContext);
        branchRoots.add(rootContext.getTypeId());
      } else {
        branchRoots = findBranchRoots(rootContext);
        LOG.debug("Did not find any parameter models for {}", rootContext.getType());
      }

      Map<ResolvedType, Model> dependencies = modelProvider.dependencies(rootContext);
      for (ResolvedType type : dependencies.keySet()) {
        ModelContext childContext = ModelContext.fromParent(rootContext, type);
        modelBranch.put(dependencies.get(type).getId(), dependencies.get(type));
        contextMap.put(dependencies.get(type).getId(), childContext);
      }

      if (modelBranch.isEmpty()) {
        continue;
      }

      final MergingContext mergingContext = createMergingContext(
          Collections.unmodifiableMap(uniqueModels),
          Collections.unmodifiableMap(parameterModelMap),
          Collections.unmodifiableMap(modelBranch),
          Collections.unmodifiableMap(contextMap));

      branchRoots.stream().filter(rootId -> modelBranch.containsKey(rootId)).forEach(
          rootId -> mergeModelBranch(adapter, mergingContext.toRootId(rootId)));

      Set<Model> updatedModels = updateModels(modelBranch.values(), contextMap, adapter);
      mergedModelMap.put(rootContext.getParameterId(), updatedModels);

      updatedModels.stream().filter(model -> !uniqueModels.containsKey(model.getName())).forEach(
          model -> {
            uniqueModels.put(model.getName(), model);
            parameterModelMap.put(model.getId(), rootContext.getParameterId());
          });
    }

    return Collections.unmodifiableMap(mergedModelMap);
  }

  private Set<ComparisonCondition> mergeModelBranch(
      UniqueTypeNameAdapter adapter,
      final MergingContext mergingContext) {

    final Set<String> nodes = collectNodes(adapter, mergingContext);

    final Set<ComparisonCondition> dependencies = new HashSet<>();
    final Set<String> currentDependencies = new HashSet<>();

    boolean allowableToSearchTheSame = true;

    final Map<String, Optional<String>> comparisonConditions = new HashMap<>();
    Set<String> parametersTo = new HashSet<>();

    for (final String modelId : nodes) {
      if (adapter.getTypeName(modelId).isPresent()) {
        continue;
      }

      if (!mergingContext.hasSeenBefore(modelId)) {

        MergingContext childMergingContext = createChildMergingContext(modelId,
            !currentDependencies.isEmpty(),
            parametersTo,
            dependencies,
            mergingContext,
            adapter);

        Set<ComparisonCondition> newDependencies = childMergingContext
            .getComparisonCondition(modelId)
            .<Set<ComparisonCondition>>map(
                reenteredCondition -> new HashSet<>(Arrays.asList(reenteredCondition)))
            .orElseGet(() -> mergeModelBranch(adapter, childMergingContext));

        if (newDependencies.isEmpty()) {
          allowableToSearchTheSame = false;
          parametersTo = new HashSet<>();
          continue;
        }

        newDependencies.stream()
            .peek(d -> checkCondition(d, false))
            .filter(d -> !d.getConditions().isEmpty())
            .forEach(p -> {
              dependencies.add(p);
              currentDependencies.addAll(p.getConditions());
            });

        if (!allowableToSearchTheSame) {
          continue;
        }

        ComparisonCondition currentCondition = currentCondition(modelId, newDependencies);

        if (currentCondition.getConditions().isEmpty()) {
          comparisonConditions.put(modelId,
              currentCondition.getModelsTo()
                  .stream()
                  .map(mergingContext::getModelParameter)
                  .findFirst());
          continue;
        }

        parametersTo = modelsToParameters(currentCondition.getModelsTo(), mergingContext);
        comparisonConditions.put(modelId, Optional.empty());
      } else {
        currentDependencies.add(modelId);

        if (!allowableToSearchTheSame) {
          continue;
        }

        Optional<Set<String>> megedParameters = mergeParameters(modelId,
            parametersTo,
            mergingContext);

        parametersTo = megedParameters.orElseGet(() -> new HashSet<>());
        allowableToSearchTheSame = megedParameters.isPresent();
        comparisonConditions.put(modelId, Optional.empty());
      }
    }

    Set<String> sameModels = new HashSet<>();
    if (allowableToSearchTheSame) {
      sameModels
          .addAll(findSameModels(comparisonConditions, parametersTo, adapter, mergingContext));
      currentDependencies.remove(mergingContext.getRootId());
    }

    return mergeConditions(sameModels, currentDependencies, dependencies, adapter, mergingContext);
  }

  private ComparisonCondition currentCondition(
      final String modelId,
      final Set<ComparisonCondition> newDependencies) {
    List<ComparisonCondition> conditions = newDependencies.stream()
        .filter(comparisonCondition -> comparisonCondition.getModelFor().equals(modelId))
        .collect(Collectors.toCollection(ArrayList::new));

    if (conditions.size() > 1) {
      throw new IllegalStateException("Ambiguous conditions for one model.");
    }

    if (conditions.size() == 0) {
      throw new IllegalStateException("Condition is not present.");
    }

    return conditions.get(0);
  }

  private Set<String> findSameModels(
      Map<String, Optional<String>> parametersMatching,
      Set<String> allowedParameters,
      UniqueTypeNameAdapter adapter,
      MergingContext mergingContext) {

    if (allowedParameters.isEmpty()) {
      allowedParameters = new HashSet<>(Arrays.asList(""));
    }

    Model rootModel = mergingContext.getRootModel();
    ModelBuilder rootModelBuilder = new ModelBuilder(rootModel);
    Set<String> sameModels = new HashSet<>();

    final String modelForTypeName = rootModel.getType().getErasedType().getName();
    final Set<Model> modelsToCompare = mergingContext.getSimilarTypeModels(modelForTypeName);

    Iterator<String> it = allowedParameters.iterator();
    while (it.hasNext()) {
      String parameter = it.next();
      List<ModelReference> subTypes = new ArrayList<>();
      for (ModelReference modelReference : rootModel.getSubTypes()) {
        Optional<String> modelId = getModelId(modelReference);

        if (modelId.isPresent()) {
          String sModelId = modelId.get();
          ModelContext modelContext = Optional.ofNullable(parametersMatching.get(sModelId))
              .map(op -> op.orElseGet(() -> parameter))
              .map(p -> pseudoContext(p, mergingContext.getModelContext(sModelId)))
              .orElseGet(() -> mergingContext.getModelContext(sModelId));
          modelReference = modelRefFactory(modelContext,
              enumTypeDeterminer,
              typeNameExtractor,
              adapter.getNames()).apply(mergingContext.getModel(sModelId).getType());
        }
        subTypes.add(modelReference);
      }

      Map<String, ModelProperty> newProperties = new HashMap<>(rootModel.getProperties());
      for (String propertyName : rootModel.getProperties().keySet()) {
        ModelProperty property = rootModel.getProperties().get(propertyName);
        ModelReference modelReference = property.getModelRef();
        Optional<String> modelId = getModelId(modelReference);

        if (modelId.isPresent()) {
          String sModelId = modelId.get();
          ModelContext modelContext = Optional.ofNullable(parametersMatching.get(sModelId))
              .map(op -> op.orElseGet(() -> parameter))
              .map(p -> pseudoContext(p, mergingContext.getModelContext(sModelId)))
              .orElseGet(() -> mergingContext.getModelContext(sModelId));
          newProperties.put(propertyName,
              new ModelPropertyBuilder(property).build()
                  .updateModelRef(modelRefFactory(modelContext,
                      enumTypeDeterminer,
                      typeNameExtractor,
                      adapter.getNames())));
        }
      }

      Model modelToCompare = rootModelBuilder.properties(newProperties).subTypes(subTypes).build();

      modelsToCompare.stream()
          .filter(m -> StringUtils.isEmpty(parameter)
              || parameter.equals(mergingContext.getModelParameter(m.getId())))
          .filter(m -> m.equalsIgnoringName(modelToCompare))
          .map(m -> m.getId())
          .findFirst()
          .ifPresent(sameModels::add);
    }

    return sameModels;
  }

  private Set<ComparisonCondition> mergeConditions(
      Set<String> sameModels,
      Set<String> currentDependencies,
      Set<ComparisonCondition> dependencies,
      UniqueTypeNameAdapter adapter,
      MergingContext mergingContext) {

    if (!sameModels.isEmpty()) {
      ComparisonCondition currentCondition = new ComparisonCondition(mergingContext.getRootId(),
          sameModels, currentDependencies);

      dependencies = filterDependencies(dependencies,
          modelsToParameters(sameModels, mergingContext),
          mergingContext);

      dependencies.add(currentCondition);

      if (currentCondition.getConditions().isEmpty()) {
        for (ComparisonCondition depComparisonCondition : dependencies) {

          Set<String> conditions = new HashSet<>(depComparisonCondition.getConditions());
          conditions.remove(currentCondition.getModelFor());
          checkCondition(
              new ComparisonCondition(depComparisonCondition.getModelFor(),
                  depComparisonCondition.getModelsTo(), conditions),
              true);
          adapter.setEqualityFor(depComparisonCondition.getModelFor(),
              new ArrayList<>(depComparisonCondition.getModelsTo()).get(0));
        }
        return new HashSet<>(Arrays.asList(currentCondition));
      } else {
        Set<ComparisonCondition> newDependencies = new HashSet<>();
        for (ComparisonCondition depComparisonCondition : dependencies) {

          Set<String> conditions = new HashSet<>(depComparisonCondition.getConditions());

          conditions.remove(currentCondition.getModelFor());
          conditions.addAll(currentCondition.getConditions());

          newDependencies.add(new ComparisonCondition(depComparisonCondition.getModelFor(),
              depComparisonCondition.getModelsTo(), conditions));
        }
        return newDependencies;
      }
    } else {
      adapter.registerUniqueType(mergingContext.getRootModel().getName(),
          mergingContext.getRootId());
      for (ComparisonCondition depComparisonCondition : dependencies) {
        String modelId = depComparisonCondition.getModelFor();
        adapter.registerUniqueType(mergingContext.getModel(modelId).getName(), modelId);
      }
    }

    return new HashSet<>();
  }

  private List<String> findBranchRoots(ModelContext rootContext) {
    List<String> roots = new ArrayList<>();

    ResolvedType resolvedType = rootContext.alternateFor(rootContext.getType());
    if (resolvedType.isArray()) {
      ResolvedType elementType = resolvedType.getArrayElementType();
      roots.addAll(findBranchRoots(ModelContext.fromParent(rootContext, elementType)));
    } else {
      for (ResolvedType parameter : resolvedType.getTypeParameters()) {
        roots.addAll(findBranchRoots(ModelContext.fromParent(rootContext, parameter)));
      }
      roots.add(ModelContext.fromParent(rootContext, resolvedType).getTypeId());
    }
    return roots;
  }

  private Set<String> collectNodes(UniqueTypeNameAdapter adapter, MergingContext mergingContext) {
    Model rootModel = mergingContext.getRootModel();
    final Set<String> nodes = new TreeSet<>();
    for (ModelReference modelReference : rootModel.getSubTypes()) {
      Optional<String> modelId = getModelId(modelReference);

      if (modelId.isPresent() && mergingContext.containsModel(modelId.get())) {
        nodes.add(modelId.get());
      }
    }

    final ModelContext rootModelContext = mergingContext
        .getModelContext(mergingContext.getRootId());
    for (ResolvedType type : rootModel.getType().getTypeParameters()) {
      String modelId = ModelContext
          .fromParent(rootModelContext, rootModelContext.alternateFor(type))
          .getTypeId();
      if (mergingContext.containsModel(modelId)) {
        nodes.add(modelId);
      }
    }

    for (ModelProperty modelProperty : rootModel.getProperties().values()) {
      Optional<String> modelId = getModelId(modelProperty.getModelRef());

      if (modelId.isPresent() && mergingContext.containsModel(modelId.get())) {
        nodes.add(modelId.get());
      }
    }

    nodes.removeIf(s -> adapter.getTypeName(s).isPresent());

    return nodes;
  }

  private Optional<Set<String>> mergeParameters(
      String modelId,
      Set<String> existingParameters,
      MergingContext mergingContext) {

    if (modelId.equals(mergingContext.getRootId())) {
      return Optional.of(existingParameters);
    }

    Set<String> parameters = new HashSet<>(existingParameters);
    for (Map.Entry<String, Set<String>> entry : mergingContext.getCircles().entrySet()) {
      if (!entry.getValue().contains(modelId)) {

        if (parameters.isEmpty()) {
          parameters.addAll(mergingContext.getCircleParameters(entry.getKey()));
        } else {
          parameters.retainAll(mergingContext.getCircleParameters(entry.getKey()));
        }

        if (parameters.isEmpty()) {
          return Optional.empty();
        }
      }
    }

    String modelForTypeName = mergingContext.getModel(modelId).getType().getErasedType().getName();
    Set<Model> similarTypeModels = mergingContext.getSimilarTypeModels(modelForTypeName);
    Set<String> candidateParameters = similarTypeModels.stream()
        .map(model -> mergingContext.getModelParameter(model.getId()))
        .collect(Collectors.toCollection(HashSet::new));

    if (parameters.isEmpty()) {
      parameters.addAll(candidateParameters);
    } else {
      parameters.retainAll(candidateParameters);
    }

    if (parameters.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(parameters);
  }

  private Set<ComparisonCondition> filterDependencies(
      Set<ComparisonCondition> dependencies,
      Set<String> parameters,
      MergingContext mergingContext) {
    return dependencies.stream().map(condition -> {
      Set<String> modelsTo = condition.getModelsTo()
          .stream()
          .filter(modelId -> parameters.contains(mergingContext.getModelParameter(modelId)))
          .collect(Collectors.toCollection(HashSet::new));
      return new ComparisonCondition(condition.getModelFor(), modelsTo, condition.getConditions());
    }).collect(Collectors.toCollection(HashSet::new));
  }

  private Model updateModel(
      Model model,
      Map<String, ModelContext> contextMap,
      UniqueTypeNameAdapter adapter) {
    for (String propertyName : model.getProperties().keySet()) {
      ModelProperty property = model.getProperties().get(propertyName);
      Optional<String> modelId = getModelId(property.getModelRef());

      if (modelId.isPresent() && contextMap.containsKey(modelId.get())) {
        property.updateModelRef(modelRefFactory(contextMap.get(modelId.get()),
            enumTypeDeterminer,
            typeNameExtractor,
            adapter.getNames()));
      }
    }
    List<ModelReference> subTypes = new ArrayList<>();
    for (ModelReference oldModelRef : model.getSubTypes()) {
      Optional<String> modelId = getModelId(oldModelRef);

      if (modelId.isPresent() && contextMap.containsKey(modelId.get())) {
        ModelContext modelContext = contextMap.get(modelId.get());
        subTypes.add(
            modelRefFactory(modelContext, enumTypeDeterminer, typeNameExtractor, adapter.getNames())
                .apply(modelContext.getType()));
      } else {
        subTypes.add(oldModelRef);
      }
    }
    String name = typeNameExtractor.typeName(contextMap.get(model.getId()), adapter.getNames());

    return new ModelBuilder(model).name(name).subTypes(subTypes).build();
  }

  private Set<Model> updateModels(
      final Collection<Model> models,
      final Map<String, ModelContext> contextMap,
      final UniqueTypeNameAdapter adapter) {
    models.forEach(model -> {

      if (!adapter.getTypeName(model.getId()).isPresent()) {
        adapter.registerUniqueType(model.getName(), model.getId());
      }
    });

    return models.stream().map(model -> updateModel(model, contextMap, adapter)).collect(
        Collectors.toCollection(HashSet::new));
  }

  private static ModelContext pseudoContext(String parameterId, ModelContext context) {
    return ModelContext.inputParam(parameterId,
        context.getGroupName(),
        context.getType(),
        Optional.empty(),
        new HashSet<>(),
        context.getDocumentationType(),
        context.getAlternateTypeProvider(),
        context.getGenericNamingStrategy(),
        Collections.unmodifiableSet(new HashSet<>()));
  }

  private static void checkCondition(
      ComparisonCondition condition,
      boolean conditionalPresenceCheck) {
    if (conditionalPresenceCheck && !condition.getConditions().isEmpty()) {
      throw new IllegalStateException("Equality with conditions is not allowed.");
    }
    if (condition.getConditions().isEmpty() && condition.getModelsTo().size() > 1) {
      throw new IllegalStateException("Ambiguous models equality when conditions is empty.");
    }
  }

  private static Optional<String> getModelId(ModelReference ref) {
    ModelReference refT = ref;
    while (true) {
      if (refT.getModelId().isPresent()) {
        return refT.getModelId();
      }
      if (refT.itemModel().isPresent()) {
        refT = refT.itemModel().get();
      } else {
        return Optional.empty();
      }
    }
  }

  private static Set<String> modelsToParameters(Set<String> models, MergingContext mergingContext) {
    return models.stream().map(mergingContext::getModelParameter).collect(
        Collectors.toCollection(HashSet::new));
  }

  @SuppressWarnings("rawtypes")
  private void markIgnorablesAsHasSeen(
      TypeResolver typeResolver,
      Set<Class> ignorableParameterTypes,
      ModelContext modelContext) {

    for (Class ignorableParameterType : ignorableParameterTypes) {
      modelContext.seen(typeResolver.resolve(ignorableParameterType));
    }
  }

  private static MergingContext createChildMergingContext(
      String modelId,
      boolean isCircle,
      Set<String> parameters,
      Set<ComparisonCondition> dependencies,
      MergingContext mergingContext,
      UniqueTypeNameAdapter adapter) {
    if (isCircle) {
      return mergingContext.toRootId(modelId, dependencies, parameters);
    } else {
      return mergingContext.toRootId(modelId);
    }
  }

  private static MergingContext createMergingContext(
      Map<String, Model> uniqueModels,
      Map<String, String> parameterModelMap,
      Map<String, Model> currentBranch,
      Map<String, ModelContext> contextMap) {
    Map<String, Set<Model>> typedModelMap = new HashMap<>();

    Iterator<Model> it = uniqueModels.values().iterator();
    while (it.hasNext()) {
      Model model = it.next();
      String rawType = model.getType().getErasedType().getName();

      Set<Model> models = new HashSet<>();
      models.add(model);

      if (typedModelMap.containsKey(rawType)) {
        models.addAll(typedModelMap.get(rawType));
      }
      typedModelMap.put(rawType, Collections.unmodifiableSet(models));
    }

    return new MergingContext(typedModelMap, parameterModelMap, currentBranch, contextMap);
  }

}