package com.mangofactory.swagger.plugins.operation;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.spring.web.plugins.OperationModelsProviderPlugin;
import com.mangofactory.spring.web.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import static com.mangofactory.schema.ResolvedTypes.*;
import static com.mangofactory.swagger.annotations.Annotations.*;

@Component
public class SwaggerOperationModelsProvider implements OperationModelsProviderPlugin {

  private static final Logger log = LoggerFactory.getLogger(SwaggerOperationModelsProvider.class);
  private final TypeResolver typeResolver;
  private final AlternateTypeProvider alternateTypeProvider;

  @Autowired
  public SwaggerOperationModelsProvider(TypeResolver typeResolver, AlternateTypeProvider alternateTypeProvider) {
    this.typeResolver = typeResolver;
    this.alternateTypeProvider = alternateTypeProvider;
  }

  @Override
  public void apply(RequestMappingContext context) {
    collectFromApiOperation(context);
    collectApiResponses(context);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private void collectFromApiOperation(RequestMappingContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    ApiOperation apiOperationAnnotation = handlerMethod.getMethodAnnotation(ApiOperation.class);
    ResolvedType modelType;
    if (null != apiOperationAnnotation && Void.class != apiOperationAnnotation.response()) {
      modelType = asResolved(typeResolver, apiOperationAnnotation.response());
      context.operationModelsBuilder().addReturn(modelType);
    }
  }

  private void collectApiResponses(RequestMappingContext context) {

    HandlerMethod handlerMethod = context.getHandlerMethod();
    Optional<ApiResponses> apiResponses = findApiResponsesAnnotations(handlerMethod.getMethod());

    log.debug("Reading parameters models for handlerMethod |{}|", handlerMethod.getMethod().getName());
    if (!apiResponses.isPresent()) {
      return;
    }

    for (ApiResponse response : apiResponses.get().value()) {
      ResolvedType modelType = alternateTypeProvider.alternateFor(asResolved(typeResolver, response.response()));
      context.operationModelsBuilder().addReturn(modelType);
    }
    log.debug("Finished reading parameters models for handlerMethod |{}|", handlerMethod.getMethod().getName());
  }
}
