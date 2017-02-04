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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;

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
    Set<ModelContext> modelContexts = pluginsManager.modelContexts(context);
    Map<String, Model> modelMap = newHashMap(context.getModelMap());
    for (ModelContext each : modelContexts) {
      markIgnorablesAsHasSeen(typeResolver, ignorableTypes, each);
      List<ModelContext> pModelContexts = modelProvider.modelsFor(each);
      if (!pModelContexts.isEmpty()) {
        compareModelMap(modelMap, pModelContexts);
      } else {
        LOG.debug("Did not find any parameter models for {}", each.getType());
      }
    }
    return modelMap;
  }

  private void compareModelMap(Map<String, Model> target, List<ModelContext> source) {     
    boolean changes;
    while (true) {      
      changes = false;
      Iterator<ModelContext> iterator = source.iterator();  
      outer:while (iterator.hasNext()) {
        ModelContext contextSource = iterator.next();
        Model modelSource = contextSource.getBuilder().build();
        LOG.debug("Checking duplicate models for model: {}", modelSource.getId());
        for (Map.Entry<String, Model> entryTarget : target.entrySet()) {
          Model modelTarger = entryTarget.getValue();
          if (!modelSource.equals(modelTarger) && modelSource.getName().equals(modelTarger.getName())) { 
            LOG.debug("Found duplicate for model: {}. Increasing index.", modelSource.getId());  
            contextSource.updateIndex(modelSource.getIndex() + 1);
            if (!contextSource.isRootContext()) {
              changes = true;
              break outer;
            }
          } 
          if (modelSource.equals(modelTarger) && !modelSource.getName().equals(modelTarger.getName())) {
            LOG.debug("Found same model for model: {}. But with different index. Adjusting the index.", modelSource.getId());  
            contextSource.updateIndex(modelTarger.getIndex());
            if (!contextSource.isRootContext()) {
              changes = true;
              break outer;
            }
          } 
        }        
      }  
      if (!changes) {
        break;  
      }
    }   
    for (ModelContext sourceModelContext : source) {
      Model model = sourceModelContext.getBuilder().build();
      if (!target.containsKey(model.getId())) {
        target.put(model.getId(), model);
      }
    }
  }

  private void markIgnorablesAsHasSeen(TypeResolver typeResolver,
                                       Set<Class> ignorableParameterTypes,
                                       ModelContext modelContext) {

    for (Class ignorableParameterType : ignorableParameterTypes) {
      modelContext.seen(typeResolver.resolve(ignorableParameterType));
    }
  }
}
