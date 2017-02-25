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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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
    LOG.debug("Starting comparing algorithm. Defining levels for the context's tree branches...");
    Multimap<Integer, ModelContext> modelTree = ArrayListMultimap.create();
    main:for (ModelContext sourceC: source) {
      Model sourceM = sourceC.getBuilder().build();
      LOG.debug("Received context with model: {}. Amount of properties: {}", 
              sourceM.getId(), 
              sourceM.getProperties().size());
      if (!sourceM.isMap()) {
        for (String key: target.keySet()) {
          if (target.get(key).equals(sourceM)) {
            sourceC.getBuilder().index(target.get(key).getIndex());
            continue main;
          }
        }
        modelTree.put(getModelLevel(sourceC), sourceC);
      }
    }
    int level = modelTree.size();
    LOG.debug("Searching for duplicates at the each tree level.");
    while (level > 0) {
      LOG.debug("Entering tree level {}...", level);
      Model previousModel = null;
      List<ModelContext> enterPoints = newArrayList(modelTree.get(level));
      for (ModelContext contextSource: enterPoints) {
        List<Model> heap = newArrayList(target.values()); 
        for (ModelContext contextS: source) {
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
      --level;
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

  private int getModelLevel(ModelContext modelContext) {
    int level = 0;
    ModelContext context = modelContext;
    while (context != null) {
      if (!context.isParentContainer() && 
          !Boolean.TRUE.equals(context.getBuilder().build().isMap())) {//!context.getBuilder().build().isMap()) {
        ++level;    
      }  
      context = context.getParent();
    }
    return level;
  }

  private void markIgnorablesAsHasSeen(TypeResolver typeResolver,
                                       Set<Class> ignorableParameterTypes,
                                       ModelContext modelContext) {

    for (Class ignorableParameterType : ignorableParameterTypes) {
      modelContext.seen(typeResolver.resolve(ignorableParameterType));
    }
  }
}
