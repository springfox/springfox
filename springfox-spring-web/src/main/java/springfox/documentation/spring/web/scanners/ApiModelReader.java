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
import java.util.Map;
import java.util.Map.Entry;
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
  public ApiModelReader(@Qualifier("cachedModels") ModelProvider modelProvider,
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
      Map<ModelContext, Model> pModel = modelProvider.modelsFor(each);
      if (!pModel.isEmpty()) {
        compareModelMap(modelMap, pModel);
      } else {
        LOG.debug("Did not find any parameter models for {}", each.getType());
      }
    }
    return modelMap;
  }

  private void compareModelMap(Map<String, Model> target, Map<ModelContext, Model> source) {
	boolean changes, deleteSame = false;
	while (true) {
	  changes = false;
	  Iterator<Entry<ModelContext, Model>> iterator = source.entrySet().iterator();  
      outer:while (iterator.hasNext()) {
    	Entry<ModelContext, Model> entrySource = iterator.next();
    	for (Map.Entry<String, Model> entryTarget : target.entrySet()) {
    	  Model modelSource = entrySource.getValue() , modelTarger = entryTarget.getValue();
     	  if (!deleteSame && !modelSource.equals(modelTarger) && modelSource.getName().equals(modelTarger.getName())) { 
    	    entrySource.setValue(entrySource.getKey().getBuilder().index(modelSource.getIndex() + 1).build());
    	    if (!entrySource.getKey().isRootContext()) {
    	      changes = true;
    	      break outer;
    	    }
    	  }	
    	  if (!deleteSame && modelSource.equals(modelTarger) && !modelSource.getName().equals(modelTarger.getName())) {
      	    entrySource.setValue(entrySource.getKey().getBuilder().index(modelTarger.getIndex()).build());
      	    if (!entrySource.getKey().isRootContext()) {
    	      changes = true;
    	      break outer;
    	    }
      	  }	
    	  if (deleteSame && modelSource.equals(modelTarger) && modelSource.getName().equals(modelTarger.getName())) {
    	    iterator.remove(); 
    	    continue outer;
    	  }
        }        
	  }  
	  if (deleteSame) {
		for (Model sourceModel : source.values()) {
		  target.put(sourceModel.getId(), sourceModel);
		}  
		break;  
	  }
	  if (!changes) {
	    deleteSame = true; 	  
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
