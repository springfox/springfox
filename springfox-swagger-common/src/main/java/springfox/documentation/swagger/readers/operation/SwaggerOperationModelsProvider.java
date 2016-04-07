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
import com.google.common.base.Function;
import com.google.common.base.Optional;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationModelsProviderPlugin;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.swagger.annotations.Annotations.*;
import static springfox.documentation.swagger.common.SwaggerPluginSupport.*;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class SwaggerOperationModelsProvider implements OperationModelsProviderPlugin {

  private static final Logger LOG = LoggerFactory.getLogger(SwaggerOperationModelsProvider.class);
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
    ResolvedType returnType = new HandlerMethodResolver(typeResolver).methodReturnType(handlerMethod);
    returnType = context.alternateFor(returnType);
    Optional<ResolvedType> returnParameter = findApiOperationAnnotation(handlerMethod.getMethod())
        .transform(resolvedTypeFromOperation(typeResolver, returnType));
    if (returnParameter.isPresent() && returnParameter.get() != returnType) {
      LOG.debug("Adding return parameter of type {}", resolvedTypeSignature(returnParameter.get()).or("<null>"));
      context.operationModelsBuilder().addReturn(returnParameter.get());
    }
  }

  private void collectApiResponses(RequestMappingContext context) {

    HandlerMethod handlerMethod = context.getHandlerMethod();
    Optional<ApiResponses> apiResponses = findApiResponsesAnnotations(handlerMethod.getMethod());

    LOG.debug("Reading parameters models for handlerMethod |{}|", handlerMethod.getMethod().getName());
    List<ResolvedType> modelTypes = apiResponses.transform(toResolvedTypes(context))
        .or(new ArrayList<ResolvedType>());
    for (ResolvedType modelType : modelTypes) {
      context.operationModelsBuilder().addReturn(modelType);
    }
  }

  private Function<ApiResponses, List<ResolvedType>> toResolvedTypes(final RequestMappingContext context) {
    return new Function<ApiResponses, List<ResolvedType>>() {
      @Override
      public List<ResolvedType> apply(ApiResponses input) {
        List<ResolvedType> resolvedTypes = newArrayList();
        for (ApiResponse response : input.value()) {
          ResolvedType modelType = context.alternateFor(typeResolver.resolve(response.response()));
          LOG.debug("Adding input parameter of type {}", resolvedTypeSignature(modelType).or("<null>"));
          resolvedTypes.add(modelType);
        }
        return resolvedTypes;
      }
    };
  }

}
