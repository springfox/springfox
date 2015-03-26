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
import com.wordnik.swagger.annotations.ApiOperation;
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
import springfox.documentation.swagger.annotations.Annotations;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

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
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }

  private void collectFromApiOperation(RequestMappingContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    ApiOperation apiOperationAnnotation = handlerMethod.getMethodAnnotation(ApiOperation.class);
    ResolvedType modelType;
    if (null != apiOperationAnnotation && Void.class != apiOperationAnnotation.response()) {
      modelType = typeResolver.resolve(apiOperationAnnotation.response());
      context.operationModelsBuilder().addReturn(modelType);
    }
  }

  private void collectApiResponses(RequestMappingContext context) {

    HandlerMethod handlerMethod = context.getHandlerMethod();
    Optional<ApiResponses> apiResponses = Annotations.findApiResponsesAnnotations(handlerMethod.getMethod());

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
