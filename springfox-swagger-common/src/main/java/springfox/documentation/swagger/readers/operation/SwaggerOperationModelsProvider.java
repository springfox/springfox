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

package springfox.documentation.swagger.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationModelsProviderPlugin;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

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
    ResolvedType returnType = context.getReturnType();
    returnType = context.alternateFor(returnType);
    Optional<ResolvedType> returnParameter = context.findAnnotation(ApiOperation.class)
        .map(resolvedTypeFromApiOperation(typeResolver, returnType));
    if (returnParameter.isPresent() && returnParameter.get() != returnType) {
      LOG.debug("Adding return parameter of type {}", resolvedTypeSignature(returnParameter.get()).orElse("<null>"));
      context.operationModelsBuilder().addReturn(returnParameter.get());
    }
  }

  private void collectApiResponses(RequestMappingContext context) {
    List<ApiResponses> allApiResponses = context.findAnnotations(ApiResponses.class);
    LOG.debug("Reading parameters models for handlerMethod |{}|", context.getName());
    Set<ResolvedType> seenTypes = new HashSet<>();
    for (ApiResponses apiResponses : allApiResponses) {
      List<ResolvedType> modelTypes = toResolvedTypes(context).apply(apiResponses);
      for (ResolvedType modelType : modelTypes) {
        if (!seenTypes.contains(modelType)) {
          seenTypes.add(modelType);
          context.operationModelsBuilder().addReturn(modelType);
        }
      }
    }
  }

  private Function<ApiResponses, List<ResolvedType>> toResolvedTypes(final RequestMappingContext context) {
    return input -> {
      List<ResolvedType> resolvedTypes = new ArrayList<>();
      for (ApiResponse response : input.value()) {
        ResolvedType modelType = context.alternateFor(typeResolver.resolve(response.response()));
        LOG.debug("Adding input parameter of type {}", resolvedTypeSignature(modelType).orElse("<null>"));
        resolvedTypes.add(modelType);
      }
      return resolvedTypes;
    };
  }

}
