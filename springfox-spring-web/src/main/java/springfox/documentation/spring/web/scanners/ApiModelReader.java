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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
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

  @SuppressWarnings({"rawtypes", "NPathComplexity"})
  public Map<String, Set<Model>> read(RequestMappingContext context) {
    Map<String, Set<Model>> mergedModelMap = new TreeMap<>();
    final UniqueTypeNameAdapter adapter = new TypeNameIndexingAdapter();

    Set<Class> ignorableTypes = new HashSet<>(context.getIgnorableParameterTypes());
    Set<ModelContext> modelContexts = pluginsManager.modelContexts(context);
    Map<String, Set<Model>> modelMap = new TreeMap<>(context.getModelMap());
    for (Set<Model> modelList : modelMap.values()) {
      for (Model model : modelList) {
        adapter.registerType(
            model.getName(),
            model.getId());
      }
    }

    MergingContext mergingContext = populateTypes(modelMap);

    for (ModelContext rootContext : modelContexts) {
      Map<String, Model> modelBranch = new HashMap<>();
      final Map<String, ModelContext> contextMap = new HashMap<>();
      markIgnorablesAsHasSeen(
          typeResolver,
          ignorableTypes,
          rootContext);
      Optional<Model> pModel = modelProvider.modelFor(rootContext);
      List<String> branchRoots = new ArrayList<>();
      if (pModel.isPresent()) {
        LOG.debug(
            "Generated parameter model id: {}, name: {}",
            pModel.get().getId(),
            pModel.get().getName());
        modelBranch.put(
            pModel.get().getId(),
            pModel.get());
        contextMap.put(
            pModel.get().getId(),
            rootContext);
        branchRoots.add(rootContext.getTypeId());
      } else {
        branchRoots = findBranchRoots(rootContext);
        LOG.debug(
            "Did not find any parameter models for {}",
            rootContext.getType());
      }

      Map<ResolvedType, Model> dependencies = modelProvider.dependencies(rootContext);
      for (ResolvedType type : dependencies.keySet()) {
        ModelContext childContext = ModelContext.fromParent(
            rootContext,
            type);
        modelBranch.put(
            dependencies.get(type).getId(),
            dependencies.get(type));
        contextMap.put(
            dependencies.get(type).getId(),
            childContext);
      }

      if (modelBranch.isEmpty()) {
        continue;
      }
      mergingContext = mergingContext.withNewBranch(
          modelBranch,
          contextMap);
      for (String rootId : branchRoots) {
        if (modelBranch.containsKey(rootId)) {
          mergeModelBranch(
              adapter,
              mergingContext.toRootId(rootId));
        }
      }

      Set<Model> updatedModels = updateModels(
          modelBranch.values(),
          contextMap,
          adapter);
      mergedModelMap.put(
          rootContext.getParameterId(),
          updatedModels);
      modelMap.put(
          rootContext.getParameterId(),
          updatedModels);
      mergingContext = populateTypes(modelMap);
    }

    return Collections.unmodifiableMap(mergedModelMap);
  }

  @SuppressWarnings({"CyclomaticComplexity", "NPathComplexity"})
  private Set<ComparisonCondition> mergeModelBranch(
      UniqueTypeNameAdapter adapter,
      MergingContext mergingContext) {

    Model rootModel = mergingContext.getRootModel();
    final Set<String> nodes = new HashSet<>();
    for (ModelReference modelReference : rootModel.getSubTypes()) {
      Optional<String> modelId = getModelId(modelReference);

      if (modelId.isPresent() && mergingContext.containsModel(modelId.get())) {
        nodes.add(modelId.get());
      }
    }

    final ModelContext rootModelContext = mergingContext.getModelContext(mergingContext.getRootId());
    for (ResolvedType type : rootModel.getType().getTypeParameters()) {
      String modelId = ModelContext.fromParent(
          rootModelContext,
          rootModelContext.alternateFor(type)).getTypeId();
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

    Optional<ComparisonCondition> currentComparisonCondition = Optional.empty();
    Set<String> sameModels = new HashSet<>();
    Set<Model> modelsToCompare = new HashSet<>();

    if (!nodes.isEmpty()) {
      mergingContext = mergeNodes(
          nodes,
          adapter,
          mergingContext);
      modelsToCompare.addAll(buildModels(
          adapter,
          mergingContext));
    } else {
      modelsToCompare.add(rootModel);
    }

    for (Model modelFor : modelsToCompare) {
      Optional<String> sameModel = findSameModels(
          modelFor,
          mergingContext);

      sameModel.ifPresent(sameModels::add);
    }

    if (!sameModels.isEmpty()) {
      currentComparisonCondition = Optional
          .of(new ComparisonCondition(
              mergingContext.getRootId(),
              sameModels,
              mergingContext.getDependencies()));
    }

    return mergeConditions(
        currentComparisonCondition,
        adapter,
        mergingContext);

  }

  @SuppressWarnings({"CyclomaticComplexity", "NPathComplexity"})
  private MergingContext mergeNodes(
      final Set<String> nodes,
      final UniqueTypeNameAdapter adapter,
      final MergingContext mergingContext) {

    boolean allowableToSearchTheSame = true;
    final Map<String, Set<String>> comparisonConditions = new HashMap<>();
    final Set<String> comparedParemeters = new HashSet<>();

    Set<ComparisonCondition> dependencies = new HashSet<>();
    final Set<String> currentDependencies = new HashSet<>();

    for (final String modelId : nodes) {
      if (adapter.getTypeName(modelId).isPresent()) {
        continue;
      }

      if (!mergingContext.hasSeenBefore(modelId)) {
        Set<ComparisonCondition> newDependencies = mergeModelBranch(
            adapter,
            mergingContext.toRootId(modelId));

        if (newDependencies.isEmpty()) {
          allowableToSearchTheSame = false;
          continue;
        }

        for (ComparisonCondition condition : newDependencies) {
          checkCondition(
              condition,
              false);
          if (!condition.getConditions().isEmpty()) {
            dependencies.add(condition);
            currentDependencies.addAll(condition.getConditions());
          }
        }

        if (!allowableToSearchTheSame) {
          continue;
        }

        ComparisonCondition currentCondition = currentCondition(
            modelId,
            newDependencies);
        Set<String> parametersTo = currentCondition.getModelsTo().stream()
            .map(mergingContext::getModelParameter)
            .collect(Collectors.toSet());

        if (currentCondition.getConditions().isEmpty()) {
          comparisonConditions.put(
              modelId,
              Collections.unmodifiableSet(parametersTo));
        } else {
          comparisonConditions.put(
              modelId,
              new HashSet<>());

          if (comparedParemeters.isEmpty()) {
            comparedParemeters.addAll(parametersTo);
          } else {
            comparedParemeters.retainAll(parametersTo);
          }

          if (comparedParemeters.isEmpty()) {
            allowableToSearchTheSame = false;
          }
        }
      } else {
        currentDependencies.add(modelId);

        if (!allowableToSearchTheSame) {
          continue;
        }

        comparisonConditions.put(
            modelId,
            new HashSet<>());
        String modelForTypeName = mergingContext.getModel(modelId).getType().getErasedType().getName();
        Set<Model> similarTypeModels = mergingContext.getSimilarTypeModels(modelForTypeName);
        Set<String> candidateParameters = similarTypeModels.stream()
            .map(model -> mergingContext.getModelParameter(model.getId()))
            .collect(Collectors.toSet());

        if (comparedParemeters.isEmpty()) {
          comparedParemeters.addAll(candidateParameters);
        } else {
          comparedParemeters.retainAll(candidateParameters);
        }

        if (comparedParemeters.isEmpty()) {
          allowableToSearchTheSame = false;
        }
      }
    }

    if (allowableToSearchTheSame) {
      comparisonConditions.putAll(populateComparisonConditions(
          comparisonConditions,
          comparedParemeters));

      dependencies = dependencies.stream()
          .map(condition -> {
            HashSet<String> tempConditions = new HashSet<>(condition.getConditions());
            tempConditions.remove(mergingContext.getRootId());
            Set<String> modelsTo = condition.getModelsTo().stream()
                .filter(modelId -> comparedParemeters.contains(mergingContext.getModelParameter(modelId)))
                .collect(Collectors.toSet());

            return new ComparisonCondition(
                condition.getModelFor(),
                modelsTo,
                tempConditions);
          })
          .collect(Collectors.toSet());
    } else {
      comparisonConditions.clear();
    }

    return mergingContext.populateDependencies(
        comparisonConditions,
        currentDependencies,
        dependencies);
  }

  private ComparisonCondition currentCondition(
      final String modelId,
      final Set<ComparisonCondition> newDependencies) {
    List<ComparisonCondition> conditions = newDependencies.stream()
        .filter(comparisonCondition -> comparisonCondition.getModelFor().equals(modelId))
        .collect(Collectors.toList());

    if (conditions.size() > 1) {
      throw new IllegalStateException("Ambiguous conditions for one model.");
    }

    Optional<ComparisonCondition> currentConditionOptional = conditions.stream().findFirst();

    if (!currentConditionOptional.isPresent()) {
      throw new IllegalStateException("Condition is not present.");
    }

    return currentConditionOptional.get();
  }

  private Map<String, Set<String>> populateComparisonConditions(
      Map<String, Set<String>> comparisonConditions,
      Set<String> comparedParameters) {
    Map<String, Set<String>> populatedComparisonConditions = new HashMap<>(comparisonConditions);
    for (String modelId : populatedComparisonConditions.keySet()) {

      if (populatedComparisonConditions.get(modelId).isEmpty()) {
        populatedComparisonConditions.put(
            modelId,
            Collections.unmodifiableSet(new TreeSet<>(comparedParameters)));
      }
    }

    return populatedComparisonConditions;
  }

  private Set<Model> buildModels(
      UniqueTypeNameAdapter adapter,
      MergingContext mergingContext) {
    Map<String, Set<String>> parametersMatching = mergingContext.getParametersMatching();
    Model rootModel = mergingContext.getRootModel();
    ModelBuilder rootModelBuilder = new ModelBuilder(rootModel);
    Set<Model> sameModels = new HashSet<>();

    if (parametersMatching.isEmpty()) {
      return new HashSet<>();
    }

    int parametersCount = parametersMatching.values().stream()
        .map(Set::size)
        .max(Integer::compareTo)
        .orElse(0);

    for (int paramIndex = 0; paramIndex < parametersCount; paramIndex++) {
      List<ModelReference> subTypes = new ArrayList<>();
      for (ModelReference modelReference : rootModel.getSubTypes()) {
        Optional<String> modelId = getModelId(modelReference);

        if (modelId.isPresent()) {
          modelReference = modelRefFunction(
              paramIndex,
              modelId.get(),
              adapter,
              mergingContext)
              .apply(mergingContext.getModel(modelId.get()).getType());
        }
        subTypes.add(modelReference);
      }

      Map<String, ModelProperty> newProperties = new HashMap<>(rootModel.getProperties());
      for (String propertyName : rootModel.getProperties().keySet()) {
        ModelProperty property = rootModel.getProperties().get(propertyName);
        ModelReference modelReference = property.getModelRef();
        Optional<String> modelId = getModelId(modelReference);

        if (modelId.isPresent()) {
          newProperties.put(
              propertyName,
              new ModelPropertyBuilder(property).build()
                  .updateModelRef(modelRefFunction(
                      paramIndex,
                      modelId.get(),
                      adapter,
                      mergingContext)));
        }
      }
      sameModels.add(rootModelBuilder.properties(newProperties).subTypes(subTypes).build());
    }

    return sameModels;
  }

  private Function<ResolvedType, ModelReference> modelRefFunction(
      int paramIndex,
      String modelId,
      UniqueTypeNameAdapter adapter,
      MergingContext mergingContext) {

    Map<String, Set<String>> parametersMatching = mergingContext.getParametersMatching();
    ModelContext context;
    if (parametersMatching.containsKey(modelId)) {
      List<String> parameters = parametersMatching.get(modelId).stream()
          .sorted()
          .collect(Collectors.toList());
      String parameter = parameters.size() == 1 ? parameters.get(0) : parameters.get(paramIndex);
      context = pseudoContext(
          parameter,
          mergingContext.getModelContext(modelId));
    } else {
      context = mergingContext.getModelContext(modelId);
    }

    return modelRefFactory(
        context,
        enumTypeDeterminer,
        typeNameExtractor,
        adapter.getNames());
  }

  private List<String> findBranchRoots(ModelContext rootContext) {
    List<String> roots = new ArrayList<>();

    ResolvedType resolvedType = rootContext.alternateFor(rootContext.getType());
    if (resolvedType.isArray()) {
      ResolvedType elementType = resolvedType.getArrayElementType();
      roots.addAll(findBranchRoots(ModelContext.fromParent(
          rootContext,
          elementType)));
    } else if (resolvedType.findSupertype(Map.class) != null || resolvedType.findSupertype(Collection.class) != null) {
      for (ResolvedType parameter : resolvedType.getTypeParameters()) {
        roots.addAll(findBranchRoots(ModelContext.fromParent(
            rootContext,
            parameter)));
      }
    } else {
      roots.add(ModelContext.fromParent(
          rootContext,
          resolvedType).getTypeId());
    }

    return roots;
  }

  private Optional<String> findSameModels(
      final Model modelFor,
      final MergingContext mergingContext) {
    String modelForTypeName = modelFor.getType().getErasedType().getName();
    Set<Model> models = mergingContext.getSimilarTypeModels(modelForTypeName);
    for (Model modelTo : models) {
      if (modelFor.equalsIgnoringName(modelTo)) {
        return Optional.of(modelTo.getId());
      }
    }

    return Optional.empty();
  }

  private Set<ComparisonCondition> mergeConditions(
      Optional<ComparisonCondition> currentComparisonCondition,
      UniqueTypeNameAdapter adapter,
      MergingContext mergingContext) {

    Set<ComparisonCondition> dependencies = new HashSet<>(mergingContext.getComparisonConditions());
    if (currentComparisonCondition.isPresent()) {
      ComparisonCondition currentCondition = currentComparisonCondition.get();
      dependencies.add(currentCondition);

      if (currentCondition.getConditions().isEmpty()) {
        for (ComparisonCondition depComparisonCondition : dependencies) {
          checkCondition(
              depComparisonCondition,
              true);
          adapter.setEqualityFor(
              depComparisonCondition.getModelFor(),
              new ArrayList<>(depComparisonCondition.getModelsTo()).get(0));
        }
      } else {
        Set<ComparisonCondition> newDependencies = new HashSet<>();
        for (ComparisonCondition depComparisonCondition : dependencies) {
          Set<String> conditions = new HashSet<>(depComparisonCondition.getConditions());
          conditions.remove(currentCondition.getModelFor());
          conditions.addAll(currentCondition.getConditions());
          newDependencies.add(new ComparisonCondition(depComparisonCondition.getModelFor(),
                                                      depComparisonCondition.getModelsTo(),
                                                      conditions));
        }
        return newDependencies;
      }
    } else {
      adapter.registerUniqueType(
          mergingContext.getRootModel().getName(),
          mergingContext.getRootId());
      for (ComparisonCondition depComparisonCondition : dependencies) {
        String modelId = depComparisonCondition.getModelFor();
        adapter.registerUniqueType(
            mergingContext.getModel(modelId).getName(),
            modelId);
      }
    }
    return currentComparisonCondition
        .map(c -> new HashSet<>(Collections.singletonList(c)))
        .orElse(new HashSet<>());
  }

  private Model updateModel(
      Model model,
      Map<String, ModelContext> contextMap,
      UniqueTypeNameAdapter adapter) {
    for (String propertyName : model.getProperties().keySet()) {
      ModelProperty property = model.getProperties().get(propertyName);
      Optional<String> modelId = getModelId(property.getModelRef());

      if (modelId.isPresent() && contextMap.containsKey(modelId.get())) {
        property.updateModelRef(
            modelRefFactory(
                contextMap.get(modelId.get()),
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
        subTypes.add(modelRefFactory(
            modelContext,
            enumTypeDeterminer,
            typeNameExtractor,
            adapter.getNames())
                         .apply(modelContext.getType()));
      } else {
        subTypes.add(oldModelRef);
      }
    }
    String name = typeNameExtractor.typeName(
        contextMap.get(model.getId()),
        adapter.getNames());

    return new ModelBuilder(model).name(name).subTypes(subTypes).build();
  }

  private Set<Model> updateModels(
      final Collection<Model> models,
      final Map<String, ModelContext> contextMap,
      final UniqueTypeNameAdapter adapter) {
    models.forEach(model -> {
      if (!adapter.getTypeName(model.getId()).isPresent()) {
        adapter.registerUniqueType(
            model.getName(),
            model.getId());
      }
    });

    return models.stream()
        .map(model -> updateModel(
            model,
            contextMap,
            adapter))
        .collect(Collectors.toSet());
  }

  @SuppressWarnings("rawtypes")
  private ModelContext pseudoContext(
      String parameterId,
      ModelContext context) {
    return ModelContext.inputParam(
        parameterId,
        context.getGroupName(),
        context.getType(),
        Optional.empty(),
        new HashSet<>(),
        context.getDocumentationType(),
        context.getAlternateTypeProvider(),
        context.getGenericNamingStrategy(),
        new HashSet<>());
  }

  private void checkCondition(
      ComparisonCondition condition,
      boolean conditionalPresenceCheck) {
    if (conditionalPresenceCheck && !condition.getConditions().isEmpty()) {
      throw new IllegalStateException("Equality with conditions is not allowed.");
    }
    if (condition.getConditions().isEmpty() && condition.getModelsTo().size() > 1) {
      throw new IllegalStateException("Ambiguous models equality when conditions is empty.");
    }
  }

  private Optional<String> getModelId(ModelReference ref) {
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

  @SuppressWarnings("rawtypes")
  private void markIgnorablesAsHasSeen(
      TypeResolver typeResolver,
      Set<Class> ignorableParameterTypes,
      ModelContext modelContext) {

    for (Class ignorableParameterType : ignorableParameterTypes) {
      modelContext.seen(typeResolver.resolve(ignorableParameterType));
    }
  }

  private static MergingContext populateTypes(Map<String, Set<Model>> modelMap) {
    Map<String, Set<Model>> typedModelMap = new HashMap<>();
    Map<String, Model> uniqueModels = new HashMap<>();
    Map<String, String> parameterModelMap = new HashMap<>();

    for (String parameterId : modelMap.keySet()) {
      for (Model model : modelMap.get(parameterId)) {
        uniqueModels.put(
            model.getName(),
            model);
        parameterModelMap.put(
            model.getId(),
            parameterId);
      }
    }

    for (Model model : uniqueModels.values()) {
      String rawType = model.getType().getErasedType().getName();

      Set<Model> tracked = new HashSet<>(Collections.singleton(model));
      if (typedModelMap.containsKey(rawType)) {
        tracked.addAll(typedModelMap.get(rawType));
      }
      typedModelMap.put(
          rawType,
          tracked);
    }

    return new MergingContext(
        "",
        typedModelMap,
        parameterModelMap,
        new HashMap<>(),
        new HashMap<>());
  }
}
