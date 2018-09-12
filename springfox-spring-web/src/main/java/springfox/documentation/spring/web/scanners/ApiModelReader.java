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

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import springfox.documentation.builders.ModelBuilder;
import springfox.documentation.schema.Model;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.ModelProvider;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static springfox.documentation.schema.ResolvedTypes.modelRefFactory;

@Component
public class ApiModelReader  {
  private static final Logger LOG = LoggerFactory.getLogger(ApiModelReader.class);
  private final ModelProvider modelProvider;
  private final TypeResolver typeResolver;
  private final DocumentationPluginsManager pluginsManager;
  private final EnumTypeDeterminer enumTypeDeterminer;
  private final TypeNameExtractor typeNameExtractor;

  @Autowired
  public ApiModelReader(@Qualifier("cachedModels") ModelProvider modelProvider,
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

  public Map<ResourceGroup, Map<String, Model>> read(ApiListingScanningContext apiListingScanningContext) {
    Map<ResourceGroup, List<RequestMappingContext>> requestMappingsByResourceGroup
            = apiListingScanningContext.getRequestMappingsByResourceGroup();

    Map<ResourceGroup, List<Model>> modelMap = newHashMap();
    Map<String, ModelContext> contextMap = newHashMap();
    for (ResourceGroup resourceGroup: requestMappingsByResourceGroup.keySet()) {
      modelMap.put(resourceGroup, new ArrayList<Model>());
      for (RequestMappingContext context: requestMappingsByResourceGroup.get(resourceGroup)) {
        Set<Class> ignorableTypes = newHashSet(context.getIgnorableParameterTypes());
        Set<ModelContext> modelContexts = pluginsManager.modelContexts(context);
        for (ModelContext rootContext : modelContexts) {
          Map<String, Model> modelBranch = newHashMap();
          markIgnorablesAsHasSeen(typeResolver, ignorableTypes, rootContext);
          Optional<Model> pModel = modelProvider.modelFor(rootContext);
          if (pModel.isPresent()) {
            LOG.debug("Generated parameter model id: {}, name: {}, schema: {} models",
                pModel.get().getId(),
                pModel.get().getName());
            modelBranch.put(pModel.get().getId(), pModel.get());
          } else {
            LOG.debug("Did not find any parameter models for {}", rootContext.getType());
          }
          modelBranch.putAll(modelProvider.dependencies(rootContext));
          for (String modelName: modelBranch.keySet()) {
            ModelContext childContext = 
                ModelContext.fromParent(rootContext, modelBranch.get(modelName).getType());
            contextMap.put(String.valueOf(childContext.hashCode()), childContext);
          }         
          modelMap.get(resourceGroup)
            .addAll(mergeModelBranch(toModelTypeMap(modelMap), modelBranch, contextMap));
        }
      }
    }

    return updateTypeNames(modelMap, contextMap);
  }

  private List<Model> mergeModelBranch(Map<String, List<Model>> modelTypeMap,
          Map<String, Model> modelBranch,
          Map<String, ModelContext> contextMap) {
    Map<String, Model> modelsToCompare;
    List<Model> newModels = new ArrayList<Model>();
    while((modelsToCompare = modelsWithoutRefs(modelBranch)).size() > 0) {
      Iterator<Map.Entry<String, Model>> it = modelsToCompare.entrySet().iterator();
      outer: while (it.hasNext()) {
        Map.Entry<String, Model> entry = it.next();
        Model model_for = entry.getValue();
        String modelForTypeName = model_for.getType().getErasedType().getName();
        if (!modelTypeMap.containsKey(modelForTypeName)) {
          continue outer;
        }
        List<Model> models = modelTypeMap.get(modelForTypeName);
        for (Model model_to : models) {
          if (!model_for.getId().equals(model_to.getId())
              && model_for.equalsIgnoringName(model_to)) {
            it.remove();
            //Putting back "correct" model to be present in the listing.
            //Needs for Swagger 1.2 because it show listings separately,
            //and doesn't merge all models in to one single map.
            newModels.add(model_to);
            ModelContext context_to = contextMap.get(model_to.getId());
            context_to.assumeEqualsTo(contextMap.get(model_for.getId()));
            modelBranch = adjustLinksFor(modelBranch, model_for.getId(), contextMap.get(model_to.getId()));
            continue outer;
          }
        }
      }
      newModels.addAll(modelsToCompare.values());
    }
    newModels.addAll(modelBranch.values());
    return newModels;
  }

  private Map<String, Model> modelsWithoutRefs(Map<String, Model> modelBranch) {
    Map<String, Model> modelsWithoutRefs = newHashMap();
    first: for (Map.Entry<String, Model> entry_model : modelBranch.entrySet()) {
      Model model = entry_model.getValue();
      if (!model.getSubTypes().isEmpty()) {
        for (ModelReference modelReference: model.getSubTypes()) {
          Optional<Integer> modelId = getModelId(modelReference);
          if (modelId.isPresent() && 
              modelBranch.containsKey(String.valueOf(modelId.get()))) {
            continue first;
          }
        }
      }
      for (Map.Entry<String, ModelProperty> entry_property : 
        model.getProperties().entrySet()) {
        ModelProperty property = entry_property.getValue();
        Optional<Integer> modelId = getModelId(property.getModelRef());
        if (modelId.isPresent() && 
            modelBranch.containsKey(String.valueOf(modelId.get()))) {
          continue first;
        }
      }
      modelsWithoutRefs.put(model.getId(), model);
    }
    modelBranch.keySet().removeAll(modelsWithoutRefs.keySet());
    return modelsWithoutRefs;
  }

  private Map<String, List<Model>> toModelTypeMap(Map<ResourceGroup, List<Model>> modelMap) {
    Map<String, List<Model>> modelTypeMap = newHashMap();
    for(Map.Entry<ResourceGroup, List<Model>> entry : modelMap.entrySet()) {
      for (Model model: entry.getValue()) {
        String typeName = model.getType().getErasedType().getName();
        if (modelTypeMap.containsKey(typeName)) {
          modelTypeMap.get(typeName).add(model);
        } else {
          modelTypeMap.put(typeName, new ArrayList<Model>(Arrays.asList(new Model[] {model})));
        }
      }
    }
    return ImmutableMap.copyOf(modelTypeMap);
  }

  private Map<String, Model> adjustLinksFor(Map<String, Model> branch,
          String id_for,
          ModelContext modelContext) {
    Map<String, Model> updatedBranch = newHashMap();
    for(Map.Entry<String, Model> entry : branch.entrySet()) {
      Model model = entry.getValue();
   // same to subTypes
      List<ModelReference> subTypes = new ArrayList<ModelReference>();
      for (ModelReference oldModelRef: model.getSubTypes()) {
        Optional<Integer> modelId = getModelId(oldModelRef);
        if (modelId.isPresent() && 
            String.valueOf(modelId.get()).equals(id_for)) {
          subTypes.add(modelRefFactory(
              ModelContext.withAdjustedTypeName(
                  modelContext), enumTypeDeterminer, typeNameExtractor).apply(modelContext.getType()));
        } else {
          subTypes.add(oldModelRef);
        }
      }
      for (Map.Entry<String, ModelProperty> property_entry : model.getProperties().entrySet()) {
        ModelProperty property = property_entry.getValue();
        Optional<Integer> modelId = getModelId(property.getModelRef());
        if (modelId.isPresent() && 
            String.valueOf(modelId.get()).equals(id_for)) {
          property.updateModelRef(modelRefFactory(modelContext, enumTypeDeterminer, typeNameExtractor));
          break;
        }
      }
      updatedBranch.put(entry.getKey(), updateModel(model, model.getName(), subTypes));
    }
    return updatedBranch;
  }

  private Map<ResourceGroup, Map<String, Model>> updateTypeNames(Map<ResourceGroup, List<Model>> modelMap,
          Map<String, ModelContext> contextMap) {
    Map<ResourceGroup, Map<String, Model>> updatedModelMap = newHashMap();
    for (ResourceGroup resourceGroup: modelMap.keySet()) {
      Map<String, Model> updatedModels = newHashMap();
      for (Model model: modelMap.get(resourceGroup)) {
        for (String propertyName: model.getProperties().keySet()) {
          ModelProperty property = model.getProperties().get(propertyName);
          Optional<Integer> modelId = getModelId(property.getModelRef());
          if (modelId.isPresent() && 
              contextMap.containsKey(String.valueOf(modelId.get()))) {
            property.updateModelRef(modelRefFactory(
                ModelContext.withAdjustedTypeName(
                    contextMap.get(String.valueOf(modelId.get()))), enumTypeDeterminer, typeNameExtractor));
          }
        }
        List<ModelReference> subTypes = new ArrayList<ModelReference>();
        for (ModelReference oldModelRef: model.getSubTypes()) {
          Optional<Integer> modelId = getModelId(oldModelRef);
          if (modelId.isPresent() && 
              contextMap.containsKey(String.valueOf(modelId.get()))) {
            ModelContext modelContext = contextMap.get(String.valueOf(modelId.get()));
            subTypes.add(modelRefFactory(
                ModelContext.withAdjustedTypeName(
                    modelContext), enumTypeDeterminer, typeNameExtractor).apply(modelContext.getType()));
          } else {
            subTypes.add(oldModelRef);
          }
        }
        String name = typeNameExtractor.typeName(
            ModelContext.withAdjustedTypeName(contextMap.get(model.getId())));
        updatedModels.put(name, updateModel(model, name, subTypes));
      }
      updatedModelMap.put(resourceGroup, updatedModels);
    }
    return updatedModelMap;
  }

  private Optional<Integer> getModelId(ModelReference ref) {
    ModelReference refT = ref;
    while (true) {
      if (refT.getModelId().isPresent()) {
        return refT.getModelId();
      }
      if (refT.itemModel().isPresent()) {
        refT = refT.itemModel().get();
      } else {
        return Optional.absent();
      }
    }
  }
  
  private Model updateModel(Model oldModel, String newName, List<ModelReference> newSubTypes) {
    return new ModelBuilder(oldModel.getId())
                   .name(newName)
                   .type(oldModel.getType())
                   .qualifiedType(oldModel.getQualifiedType())
                   .properties(oldModel.getProperties())
                   .description(oldModel.getDescription())
                   .baseModel(oldModel.getBaseModel())
                   .discriminator(oldModel.getDiscriminator())
                   .subTypes(newSubTypes)
                   .example(oldModel.getExample())
                   .xml(oldModel.getXml())
                   .build();
  }

  private void markIgnorablesAsHasSeen(TypeResolver typeResolver,
                                       Set<Class> ignorableParameterTypes,
                                       ModelContext modelContext) {

    for (Class ignorableParameterType : ignorableParameterTypes) {
      modelContext.seen(typeResolver.resolve(ignorableParameterType));
    }
  }
}