/*
 *
 *  Copyright 2015-2016 the original author or authors.
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
  private final TypeNameExtractor typeNameExtractor;

  @Autowired
  public ApiModelReader(@Qualifier("cachedModels") ModelProvider modelProvider,
          TypeResolver typeResolver,
          DocumentationPluginsManager pluginsManager,
          TypeNameExtractor typeNameExtractor) {
    this.modelProvider = modelProvider;
    this.typeResolver = typeResolver;
    this.pluginsManager = pluginsManager;
    this.typeNameExtractor = typeNameExtractor;
  }

  public Map<ResourceGroup, List<Model>> read(ApiListingScanningContext apiListingScanningContext) {
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
          if (model_for.equalsIgnoringName(model_to)) {
            it.remove();
            //Putting back "correct" model to be present in the listing.
            //Needs for Swagger 1.2 because it show listings separately,
            //and doesn't merge all models in to one single map.
            newModels.add(model_to);
            ModelContext context_to = contextMap.get(model_to.getId());
            context_to.assumeEqualsTo(contextMap.get(model_for.getId()));
            adjustLinksFor(modelBranch, model_for.getId(), contextMap.get(model_to.getId()));
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
      for (Map.Entry<String, ModelProperty> entry_property : 
        model.getProperties().entrySet()) {
          ModelProperty property = entry_property.getValue();
          if (property.getModelRef().getModelId().isPresent()) {
            String id = String.valueOf(property.getModelRef().getModelId().get());
            if (modelBranch.containsKey(id)) {
              continue first;
            }
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

  private void adjustLinksFor(Map<String, Model> branch,
          String id_for,
          ModelContext modelContext) {
    for(Map.Entry<String, Model> entry : branch.entrySet()) {
      Model model = entry.getValue();
      for (Map.Entry<String, ModelProperty> property_entry : model.getProperties().entrySet()) {
        ModelProperty property = property_entry.getValue();
        if (property.getModelRef().getModelId().isPresent() &&
            String.valueOf(property.getModelRef().getModelId().get()).equals(id_for)) {
          property.updateModelRef(modelRefFactory(modelContext, typeNameExtractor));
        }
      }
    }
  }
  
  private Map<ResourceGroup, List<Model>> updateTypeNames(Map<ResourceGroup, List<Model>> modelMap,
          Map<String, ModelContext> contextMap) {
    Map<ResourceGroup, List<Model>> updatedModelMap = newHashMap();
    for (ResourceGroup resourceGroup: modelMap.keySet()) {
      List<Model> updatedModels = new ArrayList<Model>();
      for (Model model: modelMap.get(resourceGroup)) {
        for (String propertyName: model.getProperties().keySet()) {
          ModelProperty property = model.getProperties().get(propertyName);
          ModelReference ref = property.getModelRef();

          while (true) {
            if (ref.getModelId().isPresent() && 
                    contextMap.containsKey(String.valueOf(ref.getModelId().get()))) {
              property.updateModelRef(modelRefFactory(
                  ModelContext.withAdjustedTypeName(
                      contextMap.get(String.valueOf(ref.getModelId().get()))), typeNameExtractor));
              break;
            }
            if (ref.itemModel().isPresent()) {
              ref = ref.itemModel().get();
            } else {
              break;
            }
          }
        }
        updatedModels.add(new ModelBuilder(model.getId())
            .name(typeNameExtractor.typeName(
                ModelContext.withAdjustedTypeName(contextMap.get(model.getId()))))
            .type(model.getType())
            .qualifiedType(model.getQualifiedType())
            .properties(model.getProperties())
            .description(model.getDescription())
            .baseModel(model.getBaseModel())
            .discriminator(model.getDiscriminator())
            .subTypes(model.getSubTypes())
            .example(model.getExample())
            .build());

      }
      updatedModelMap.put(resourceGroup, updatedModels);
    }
    return updatedModelMap;
  }

  private void markIgnorablesAsHasSeen(TypeResolver typeResolver,
                                       Set<Class> ignorableParameterTypes,
                                       ModelContext modelContext) {

    for (Class ignorableParameterType : ignorableParameterTypes) {
      modelContext.seen(typeResolver.resolve(ignorableParameterType));
    }
  }
}
