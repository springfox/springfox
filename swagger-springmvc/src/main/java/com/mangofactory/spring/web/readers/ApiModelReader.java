package com.mangofactory.spring.web.readers;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.mangofactory.schema.ModelProvider;
import com.mangofactory.schema.plugins.ModelContext;
import com.mangofactory.service.model.Model;
import com.mangofactory.service.model.ModelProperty;
import com.mangofactory.service.model.builder.ModelBuilder;
import com.mangofactory.spring.web.plugins.DocumentationPluginsManager;
import com.mangofactory.spring.web.scanners.RequestMappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;
import static com.mangofactory.schema.ResolvedTypes.*;

@Component
public class ApiModelReader implements Command<RequestMappingContext> {
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

  @Override
  public void execute(RequestMappingContext outerContext) {

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
    outerContext.setModelMap(modelMap);
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
      modelContext.seen(asResolved(typeResolver, ignorableParameterType));
    }
  }

  private void populateDependencies(ModelContext modelContext, Map<String, Model> modelMap) {
    Map<String, Model> dependencies = modelProvider.dependencies(modelContext);
    for (Model each : dependencies.values()) {
      mergeModelMap(modelMap, each);
    }
  }

}
