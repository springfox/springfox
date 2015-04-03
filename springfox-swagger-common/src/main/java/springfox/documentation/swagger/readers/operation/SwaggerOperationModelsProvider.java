/*
 *
 *  Copyright 2015 the original author or authors.
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

package springfox.documentation.swagger.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationModelsProviderPlugin;
import springfox.documentation.spi.service.contexts.RequestMappingContext;

import static springfox.documentation.spring.web.HandlerMethodReturnTypes.*;
import static springfox.documentation.swagger.annotations.Annotations.*;
import static springfox.documentation.swagger.common.SwaggerPluginSupport.*;

@Component
public class SwaggerOperationModelsProvider implements OperationModelsProviderPlugin {

  private static final Logger log = LoggerFactory.getLogger(SwaggerOperationModelsProvider.class);
  private final TypeResolver typeResolver;

  @Autowired
  public SwaggerOperationModelsProvider(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }

  @Override
  public void apply(RequestMappingContext context) {
    collectFromApiOperation(context);
    collectApiResponses(context);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }

  private void collectFromApiOperation(RequestMappingContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    ResolvedType returnType = handlerReturnType(typeResolver, handlerMethod);
    returnType = context.alternateFor(returnType);
    Optional<ResolvedType> optional = findApiOperationAnnotation(handlerMethod.getMethod())
        .transform(resolvedTypeFromOperation(typeResolver, returnType));
    if (optional.isPresent() && optional.get() != returnType) {
      context.operationModelsBuilder().addReturn(optional.get());
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
      ResolvedType modelType = context.alternateFor(typeResolver.resolve(response.response()));
      context.operationModelsBuilder().addReturn(modelType);
    }
    log.debug("Finished reading parameters models for handlerMethod |{}|", handlerMethod.getMethod().getName());
  }
}
