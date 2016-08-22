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
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ModelBuilder;
import springfox.documentation.schema.Model;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.ModelProvider;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

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
      Optional<Model> pModel = modelProvider.modelFor(each);
      if (pModel.isPresent()) {
        LOG.debug("Generated parameter model id: {}, name: {}, schema: {} models",
            pModel.get().getId(),
            pModel.get().getName());
        mergeModelMap(modelMap, pModel.get());
      } else {
        LOG.debug("Did not find any parameter models for {}", each.getType());
      }
      populateDependencies(each, modelMap);
    }
    return modelMap;
  }

  @SuppressWarnings("unchecked")
  private void mergeModelMap(Map<String, Model> target, Model source) {
      String sourceModelKey = source.getId();

      if (!target.containsKey(sourceModelKey)) {
        //if we encounter completely unknown model, just add it
        LOG.debug("Adding a new model with key {}", sourceModelKey);
        target.put(sourceModelKey, source);
      } else {
        //we can encounter a known model with an unknown property
        //if (de)serialization is not symmetrical (@JsonIgnore on setter, @JsonProperty on getter).
        //In these cases, don't overwrite the entire model entry for that type, just add the unknown property.
        Model targetModelValue = target.get(sourceModelKey);

        Map<String, ModelProperty> targetProperties = targetModelValue.getProperties();
        Map<String, ModelProperty> sourceProperties = source.getProperties();

        Set<String> newSourcePropKeys = newHashSet(sourceProperties.keySet());
        newSourcePropKeys.removeAll(targetProperties.keySet());
        Map<String, ModelProperty> mergedTargetProperties = Maps.newHashMap(targetProperties);
        for (String newProperty : newSourcePropKeys) {
          LOG.debug("Adding a missing property {} to model {}", newProperty, sourceModelKey);
          mergedTargetProperties.put(newProperty, sourceProperties.get(newProperty));
        }

        Model mergedModel = new ModelBuilder()
                .id(targetModelValue.getId())
                .name(targetModelValue.getName())
                .type(targetModelValue.getType())
                .qualifiedType(targetModelValue.getQualifiedType())
                .properties(mergedTargetProperties)
                .description(targetModelValue.getDescription())
                .baseModel(targetModelValue.getBaseModel())
                .discriminator(targetModelValue.getDiscriminator())
                .subTypes(targetModelValue.getSubTypes())
                .example(targetModelValue.getExample())
                .build();

        target.put(sourceModelKey, mergedModel);
      }
  }

  private void markIgnorablesAsHasSeen(TypeResolver typeResolver,
                                       Set<Class> ignorableParameterTypes,
                                       ModelContext modelContext) {

    for (Class ignorableParameterType : ignorableParameterTypes) {
      modelContext.seen(typeResolver.resolve(ignorableParameterType));
    }
  }

  private void populateDependencies(ModelContext modelContext, Map<String, Model> modelMap) {
    Map<String, Model> dependencies = modelProvider.dependencies(modelContext);
    for (Model each : dependencies.values()) {
      mergeModelMap(modelMap, each);
    }
  }

}
