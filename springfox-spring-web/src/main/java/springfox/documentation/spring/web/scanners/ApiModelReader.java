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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.Model;
import springfox.documentation.schema.ModelProvider;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;
import static com.google.common.collect.Lists.*;

@Component
public class ApiModelReader  {
  private static final Logger LOG = LoggerFactory.getLogger(ApiModelReader.class);
  private final ModelProvider modelProvider;
  private final TypeResolver typeResolver;
  private final DocumentationPluginsManager pluginsManager;

  @Autowired
  public ApiModelReader(@Qualifier("default") ModelProvider modelProvider,
          TypeResolver typeResolver,
          DocumentationPluginsManager pluginsManager) {
    this.modelProvider = modelProvider;
    this.typeResolver = typeResolver;
    this.pluginsManager = pluginsManager;
  }

  public Map<String, Model> read(RequestMappingContext context) {

    Set<Class> ignorableTypes = newHashSet(context.getIgnorableParameterTypes());
    List<ModelContext> modelContexts = pluginsManager.modelContexts(context);
    Map<String, Model> globalModelMap = newHashMap(context.getModelMap());
    Map<String, Model> localModelMap = newHashMap();
    for (ModelContext each : modelContexts) {
      markIgnorablesAsHasSeen(typeResolver, ignorableTypes, each);
      List<ModelContext> pModelContexts = modelProvider.modelsFor(each);
      if (!pModelContexts.isEmpty()) {
        localModelMap.putAll(compareModelMap(globalModelMap, pModelContexts));
      } else {
        LOG.debug("Did not find any parameter models for {}", each.getType());
      }
    }
    return localModelMap;
  }

  private Map<String, Model> compareModelMap(Map<String, Model> target, List<ModelContext> source) {  
    LOG.debug("Starting comparing algorithm. Defining enter points for the context's tree branches...");
    List<ModelContext> enterPoints = newArrayList();
    main:for (ModelContext sourceC: source) {
      Model modelSource = sourceC.getBuilder().build();  
      LOG.debug("Received context with model: {}. Amount of properties: {}", 
              modelSource.getId(), 
              modelSource.getProperties().size());
      if (modelSource.isMap()) {
        continue;
      }
      for (ModelContext sourceT: source) {
        ModelContext parent = toParent(sourceT.getParent(), source);
        if (sourceC == parent) {
          continue main;
        }
      }
      LOG.debug("Model: {} is in the lowest level at the tree, added to the entry points.", modelSource.getId());
      enterPoints.add(sourceC);    
    }
    LOG.debug("Searching for duplicates at the current tree level.");
    while (enterPoints.size() != 0) {
      Model previousModel = null;
      for (ModelContext contextSource: enterPoints ) {
        List<Model> heap = newArrayList(target.values()); 
        for (ModelContext contextS: enterPoints) {
          Model modelSource = contextS.getBuilder().build();
          if (previousModel != null && previousModel.equals(modelSource)) {
            LOG.debug("Model: {} has already been checked. Increasing index.", modelSource.getId());  
            contextS.updateIndex(previousModel.getIndex()); 
            modelSource = contextS.getBuilder().build();
          }
          heap.add(modelSource); 
        }
        Model modelSource = contextSource.getBuilder().build();
        LOG.debug("Checking duplicate models for model: {}({})", modelSource.getId(), modelSource.getProperties().size());
        for (int i = 0; i < heap.size(); i++) {
          Model modelTarger = heap.get(i);
          LOG.debug("Comparing with model: {}({})", modelTarger.getId(), modelTarger.getProperties().size());
          if (!modelSource.equals(modelTarger) && modelSource.getName().equals(modelTarger.getName())) { 
            LOG.debug("Found duplicate for model: {}. Increasing index.", modelSource.getId());  
            contextSource.updateIndex(modelSource.getIndex() + 1);
            modelSource = contextSource.getBuilder().build();
            i = 0;
          }
        }   
        previousModel = modelSource;
      }   
      LOG.debug("Going to the next tree's level.");
      enterPoints = nextLevel(enterPoints, source);
    }
    Map<String, Model> localModels = newHashMap();
    for (ModelContext sourceModelContext : source) {
      Model model = sourceModelContext.getBuilder().build();
      if (!target.containsKey(model.getId())) {
        target.put(model.getId(), model);
      }
      localModels.put(model.getId(), model);
    }
    return localModels;
  }

  private boolean containsInTree(ModelContext context, List<ModelContext> contexts) {
    for (ModelContext contextT: contexts) {
      if (context == contextT) {
        return true;
      }
    }
    return false;
  }

  private ModelContext toParent(ModelContext context, List<ModelContext> contexts) {
    while (context != null) {
      if (!containsInTree(context, contexts)) {
        context = context.getParent();
      } else {
          break;
        }
    }
    return context;
  }
  
  private List<ModelContext> nextLevel(List<ModelContext> currentLevel, List<ModelContext> allModels) {
    List<ModelContext> enterPointsNext = newArrayList();
    for (ModelContext context: currentLevel) {
      ModelContext parent = toParent(context.getParent(), allModels);
      if (parent != null) {
        Model modelParent = parent.getBuilder().build();
          if (!modelParent.isMap()) {
          LOG.debug("Model: {}() is in the next level at the tree, added to the entry points.", 
                  modelParent.getId(), 
                  modelParent.getProperties().size());
          enterPointsNext.add(parent);
        }
      }
    }
    return enterPointsNext;
  }

  private void markIgnorablesAsHasSeen(TypeResolver typeResolver,
                                       Set<Class> ignorableParameterTypes,
                                       ModelContext modelContext) {

    for (Class ignorableParameterType : ignorableParameterTypes) {
      modelContext.seen(typeResolver.resolve(ignorableParameterType));
    }
  }
}
