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

package springfox.documentation.spring.web.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import springfox.documentation.schema.plugins.SchemaPluginsManager;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ViewProviderPlugin;
import springfox.documentation.spi.service.OperationModelsProviderPlugin;
import springfox.documentation.spi.service.contexts.RequestMappingContext;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static springfox.documentation.schema.ResolvedTypes.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OperationModelsProvider implements OperationModelsProviderPlugin {

  private static final Logger LOG = LoggerFactory.getLogger(OperationModelsProvider.class);
  private final SchemaPluginsManager pluginsManager;

  @Autowired
  public OperationModelsProvider(SchemaPluginsManager pluginsManager) {
    this.pluginsManager = pluginsManager;
  }

  @Override
  public void apply(RequestMappingContext context) {
    collectFromReturnType(context);
    collectParameters(context);
    collectGlobalModels(context);
  }

  private void collectGlobalModels(RequestMappingContext context) {
    for (ResolvedType each : context.getAdditionalModels()) {
      context.operationModelsBuilder().addInputParam(each);
      context.operationModelsBuilder().addReturn(each);
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private void collectFromReturnType(RequestMappingContext context) {
    ResolvedType modelType = context.getReturnType();
    modelType = context.alternateFor(modelType);
    LOG.debug(
        "Adding return parameter of type {}",
        resolvedTypeSignature(modelType).orElse("<null>"));

    context.operationModelsBuilder()
           .addReturn(
               modelType,
               viewForReturn(context));
  }

  private void collectParameters(RequestMappingContext context) {
    LOG.debug(
        "Reading parameters models for handlerMethod |{}|",
        context.getName());

    List<ResolvedMethodParameter> parameterTypes = context.getParameters();
    for (ResolvedMethodParameter parameterType : parameterTypes) {
      if (parameterType.hasParameterAnnotation(RequestBody.class)
          || parameterType.hasParameterAnnotation(RequestPart.class)) {
        ResolvedType modelType = context.alternateFor(parameterType.getParameterType());
        LOG.debug(
            "Adding input parameter of type {}",
            resolvedTypeSignature(modelType).orElse("<null>"));
        context.operationModelsBuilder().addInputParam(
            modelType,
            viewForParameter(
                context,
                parameterType),
            new HashSet<>());
      }
    }
    LOG.debug(
        "Finished reading parameters models for handlerMethod |{}|",
        context.getName());
  }

  private Optional<ResolvedType> viewForReturn(RequestMappingContext context) {
    ViewProviderPlugin viewProvider =
        pluginsManager.viewProvider(context.getDocumentationContext().getDocumentationType());
    return viewProvider.viewFor(
        context);
  }

  private Optional<ResolvedType> viewForParameter(
      RequestMappingContext context,
      ResolvedMethodParameter parameter) {
    ViewProviderPlugin viewProvider =
        pluginsManager.viewProvider(context.getDocumentationContext().getDocumentationType());
    return viewProvider.viewFor(
        parameter);
  }
}
