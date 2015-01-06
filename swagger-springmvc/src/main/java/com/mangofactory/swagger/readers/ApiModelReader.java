package com.mangofactory.swagger.readers;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.mangofactory.documentation.plugins.DocumentationType;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.core.ModelUtils;
import com.mangofactory.schema.Annotations;
import com.mangofactory.schema.ModelContext;
import com.mangofactory.schema.ModelProvider;
import com.mangofactory.service.model.builder.ModelBuilder;
import com.mangofactory.swagger.readers.operation.HandlerMethodResolver;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.mangofactory.service.model.Model;
import com.mangofactory.service.model.ModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;
import static com.mangofactory.schema.ResolvedTypes.*;

@Component
public class ApiModelReader implements Command<RequestMappingContext> {
  private static final Logger log = LoggerFactory.getLogger(ApiModelReader.class);
  private final ModelProvider modelProvider;
  private final AlternateTypeProvider alternateTypeProvider;
  private final TypeResolver typeResolver;

  @Autowired
  public ApiModelReader(ModelProvider modelProvider,
          AlternateTypeProvider alternateTypeProvider,
          TypeResolver typeResolver) {
    this.modelProvider = modelProvider;
    this.alternateTypeProvider = alternateTypeProvider;
    this.typeResolver = typeResolver;
  }

  @Override
  public void execute(RequestMappingContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();

    log.debug("Reading models for handlerMethod |{}|", handlerMethod.getMethod().getName());

    Map<String, Model> modelMap = context.getModelMap();
    HandlerMethodResolver handlerMethodResolver = new HandlerMethodResolver(typeResolver);
    ResolvedType modelType = ModelUtils.handlerReturnType(typeResolver, handlerMethod);
    modelType = alternateTypeProvider.alternateFor(modelType);

    ApiOperation apiOperationAnnotation = handlerMethod.getMethodAnnotation(ApiOperation.class);
    if (null != apiOperationAnnotation && Void.class != apiOperationAnnotation.response()) {
      modelType = asResolved(typeResolver, apiOperationAnnotation.response());
    }
    Set<Class> ignorableTypes = context.getDocumentationContext().getIgnorableParameterTypes();
    DocumentationType documentationType = context.getDocumentationContext().getDocumentationType();
    if (!ignorableTypes.contains(modelType.getErasedType())) {
      ModelContext modelContext = ModelContext.returnValue(modelType,
              documentationType);
      markIgnorablesAsHasSeen(typeResolver, ignorableTypes, modelContext);
      Optional<Model> model = modelProvider.modelFor(modelContext);
      if (model.isPresent() && !"void".equals(model.get().getName())) {
        log.debug("Swagger generated parameter model id: {}, name: {}, schema: {} models",
                model.get().getId(),
                model.get().getName());
        modelMap.put(model.get().getId(), model.get());
      } else {
        log.debug("Swagger core did not find any models");
      }
      populateDependencies(modelContext, modelMap);
    }
    mergeModelMap(modelMap, readParametersApiModel(handlerMethodResolver, handlerMethod,
            ignorableTypes, documentationType));
    mergeModelMap(modelMap, readApiResponses(handlerMethod, ignorableTypes,
            documentationType));

    log.debug("Finished reading models for handlerMethod |{}|", handlerMethod.getMethod().getName());
  }

  private Map<String, Model> readApiResponses(HandlerMethod handlerMethod, Set<Class> ignorableTypes,
                                              DocumentationType documentationType) {

    Optional<ApiResponses> apiResponses = Annotations.findApiResponsesAnnotations(handlerMethod.getMethod());
    Map<String, Model> modelMap = newHashMap();

    log.debug("Reading parameters models for handlerMethod |{}|", handlerMethod.getMethod().getName());
    if (!apiResponses.isPresent()) {
      return modelMap;
    }

    for (ApiResponse response : apiResponses.get().value()) {
          if (!ignorableTypes.contains(response.response())) {
            ResolvedType modelType = alternateTypeProvider.alternateFor(asResolved(typeResolver, response.response()));
            ModelContext modelContext = ModelContext.returnValue(modelType, documentationType);
            markIgnorablesAsHasSeen(typeResolver, ignorableTypes,  modelContext);
            Optional<Model> pModel = modelProvider.modelFor(modelContext);
            if (pModel.isPresent()) {
              log.debug("Swagger generated parameter model id: {}, name: {}, schema: {} models",
                      pModel.get().getId(),
                      pModel.get().getName());
              modelMap.put(pModel.get().getId(), pModel.get());
            } else {
              log.debug("Swagger core did not find any parameter models for {}", response.response());
            }
            populateDependencies(modelContext, modelMap);
      }
    }
    log.debug("Finished reading parameters models for handlerMethod |{}|", handlerMethod.getMethod().getName());
    return modelMap;
  }

  @SuppressWarnings("unchecked")
  private void mergeModelMap(Map<String, Model> target, Map<String, Model> source) {
    for (Map.Entry<String, Model> sModelEntry : source.entrySet()) {
      String sourceModelKey = sModelEntry.getKey();

      if (!target.containsKey(sourceModelKey)) {
        //if we encounter completely unknown model, just add it
        target.put(sModelEntry.getKey(), sModelEntry.getValue());
      } else {
        //we can encounter a known model with an unknown property
        //if (de)serialization is not symmetrical (@JsonIgnore on setter, @JsonProperty on getter).
        //In these cases, don't overwrite the entire model entry for that type, just add the unknown property.
        Model targetModelValue = target.get(sourceModelKey);
        Model sourceModelValue = sModelEntry.getValue();

        Map<String, ModelProperty> targetProperties = targetModelValue.getProperties();
        Map<String, ModelProperty> sourceProperties = sourceModelValue.getProperties();

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
  }

  private void markIgnorablesAsHasSeen(TypeResolver typeResolver, Set<Class> ignorableParameterTypes,
                                       ModelContext modelContext) {

    for (Class ignorableParameterType : ignorableParameterTypes) {
      modelContext.seen(asResolved(typeResolver, ignorableParameterType));
    }
  }

  private Map<String, Model> readParametersApiModel(HandlerMethodResolver handlerMethodResolver,
                                                    HandlerMethod handlerMethod, Set<Class> ignorableTypes,
                                                    DocumentationType documentationType) {

    Method method = handlerMethod.getMethod();
    Map<String, Model> modelMap = newHashMap();

    log.debug("Reading parameters models for handlerMethod |{}|", handlerMethod.getMethod().getName());

    List<ResolvedMethodParameter> parameterTypes = handlerMethodResolver.methodParameters(handlerMethod);
    Annotation[][] annotations = method.getParameterAnnotations();

    for (int i = 0; i < annotations.length; i++) {
      Annotation[] pAnnotations = annotations[i];
      for (Annotation annotation : pAnnotations) {
        if (annotation instanceof RequestBody) {
          ResolvedMethodParameter pType = parameterTypes.get(i);
          if (!ignorableTypes .contains(pType.getResolvedParameterType().getErasedType())) {
            ResolvedType modelType = alternateTypeProvider.alternateFor(pType.getResolvedParameterType());
            ModelContext modelContext = ModelContext.inputParam(modelType, documentationType);
            markIgnorablesAsHasSeen(typeResolver, ignorableTypes, modelContext);
            Optional<Model> pModel = modelProvider.modelFor(modelContext);
            if (pModel.isPresent()) {
              log.debug("Swagger generated parameter model id: {}, name: {}, schema: {} models",
                      pModel.get().getId(),
                      pModel.get().getName());
              modelMap.put(pModel.get().getId(), pModel.get());
            } else {
              log.debug("Swagger core did not find any parameter models for {}",
                      pType.getResolvedParameterType());
            }
            populateDependencies(modelContext, modelMap);
          }
        }
      }
    }
    log.debug("Finished reading parameters models for handlerMethod |{}|", handlerMethod.getMethod().getName());
    return modelMap;
  }

  private void populateDependencies(ModelContext modelContext, Map<String, Model> modelMap) {
    modelMap.putAll(modelProvider.dependencies(modelContext));
  }

}
