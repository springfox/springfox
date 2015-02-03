package com.mangofactory.documentation.spring.web.scanners;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.mangofactory.documentation.schema.ModelProvider;
import com.mangofactory.documentation.schema.Model;
import com.mangofactory.documentation.schema.ModelProperty;
import com.mangofactory.documentation.builder.ModelBuilder;
import com.mangofactory.documentation.spi.schema.contexts.ModelContext;
import com.mangofactory.documentation.spring.web.plugins.DocumentationPluginsManager;
import com.mangofactory.documentation.spi.service.contexts.RequestMappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;

@Component
public class ApiModelReader  {
  private static final Logger log = LoggerFactory.getLogger(ApiModelReader.class);
  private final ModelProvider modelProvider;
  private final TypeResolver typeResolver;
  private final DocumentationPluginsManager pluginsManager;

  @Autowired
  public ApiModelReader(ModelProvider modelProvider,
          TypeResolver typeResolver,
          DocumentationPluginsManager pluginsManager) {
    this.modelProvider = modelProvider;
    this.typeResolver = typeResolver;
    this.pluginsManager = pluginsManager;
  }

  public Map<String, Model> read(RequestMappingContext outerContext) {

    Set<Class> ignorableTypes = newHashSet(outerContext.getDocumentationContext().getIgnorableParameterTypes());
    Set<ModelContext> modelContexts = pluginsManager.modelContexts(outerContext);
    Map<String, Model> modelMap = newHashMap();
    for (ModelContext each : modelContexts) {
      markIgnorablesAsHasSeen(typeResolver, ignorableTypes, each);
      Optional<Model> pModel = modelProvider.modelFor(each);
      if (pModel.isPresent()) {
        log.debug("Generated parameter model id: {}, name: {}, schema: {} models",
                pModel.get().getId(),
                pModel.get().getName());
        mergeModelMap(modelMap, pModel.get());
      } else {
        log.debug("Did not find any parameter models for {}", each.getType());
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
