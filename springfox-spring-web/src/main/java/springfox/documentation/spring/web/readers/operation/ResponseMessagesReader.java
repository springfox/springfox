/*
 *
 *  Copyright 2015-2018 the original author or authors.
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.schema.plugins.SchemaPluginsManager;
import springfox.documentation.schema.property.ModelSpecificationFactory;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.ViewProviderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ResponseContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.*;
import static springfox.documentation.schema.ResolvedTypes.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@SuppressWarnings("deprecation")
public class ResponseMessagesReader implements OperationBuilderPlugin {

  private final EnumTypeDeterminer enumTypeDeterminer;
  private final TypeNameExtractor typeNameExtractor;
  private final SchemaPluginsManager pluginsManager;
  private final ModelSpecificationFactory modelSpecifications;
  private final DocumentationPluginsManager documentationPlugins;

  @Autowired
  public ResponseMessagesReader(
      EnumTypeDeterminer enumTypeDeterminer,
      TypeNameExtractor typeNameExtractor,
      SchemaPluginsManager pluginsManager,
      ModelSpecificationFactory modelSpecifications,
      DocumentationPluginsManager documentationPlugins) {
    this.enumTypeDeterminer = enumTypeDeterminer;
    this.typeNameExtractor = typeNameExtractor;
    this.pluginsManager = pluginsManager;
    this.modelSpecifications = modelSpecifications;
    this.documentationPlugins = documentationPlugins;
  }

  @Override
  public void apply(OperationContext context) {
    List<springfox.documentation.service.ResponseMessage> responseMessages
        = context.getGlobalResponseMessages(context.httpMethod().toString());
    context.operationBuilder().responseMessages(new HashSet<>(responseMessages));

    context.operationBuilder().responses(new HashSet<>(context.globalResponsesFor(context.httpMethod())));
    applyReturnTypeOverride(context);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private void applyReturnTypeOverride(OperationContext context) {
    ResolvedType returnType = context.alternateFor(context.getReturnType());
    int httpStatusCode = httpStatusCode(context);
    String message = message(context);
    springfox.documentation.schema.ModelReference modelRef = null;

    ViewProviderPlugin viewProvider =
        pluginsManager.viewProvider(context.getDocumentationContext().getDocumentationType());

    ResponseContext responseContext = new ResponseContext(context.getDocumentationContext(), context);
    if (!isVoid(returnType)) {
      ModelContext modelContext = context.operationModelsBuilder()
          .addReturn(returnType,
              viewProvider.viewFor(context));

      Map<String, String> knownNames = new HashMap<>();
      Optional.ofNullable(context.getKnownModels().get(modelContext.getParameterId()))
          .orElse(new HashSet<>())
          .forEach(model -> knownNames.put(
              model.getId(),
              model.getName()));

      modelRef = modelRefFactory(
          modelContext,
          enumTypeDeterminer,
          typeNameExtractor,
          knownNames).apply(returnType);
      Set<MediaType> produces = new HashSet<>(context.produces());
      if (produces.isEmpty()) {
        produces.add(MediaType.ALL);
      }
      produces
          .forEach(mediaType ->
              responseContext.responseBuilder()
                  .representation(mediaType)
                  .apply(r -> r.model(m -> m.copyOf(modelSpecifications.create(modelContext, returnType)))));
    }
    springfox.documentation.service.ResponseMessage built =
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(httpStatusCode)
            .message(message)
            .responseModel(modelRef)
            .build();

    responseContext.responseBuilder()
        .description(message)
        .code(String.valueOf(httpStatusCode));

    context.operationBuilder()
        .responseMessages(singleton(built));
    context.operationBuilder()
        .responses(Collections.singleton(documentationPlugins.response(responseContext)));
  }

  public static int httpStatusCode(OperationContext context) {
    Optional<ResponseStatus> responseStatus = context.findAnnotation(ResponseStatus.class);
    int httpStatusCode = HttpStatus.OK.value();
    if (responseStatus.isPresent()) {
      httpStatusCode = responseStatus.get().value().value();
    }
    return httpStatusCode;
  }

  public static String message(OperationContext context) {
    Optional<ResponseStatus> responseStatus = context.findAnnotation(ResponseStatus.class);
    String reasonPhrase = HttpStatus.OK.getReasonPhrase();
    if (responseStatus.isPresent()) {
      reasonPhrase = responseStatus.get().reason();
      if (reasonPhrase.isEmpty()) {
        reasonPhrase = responseStatus.get().value().getReasonPhrase();
      }
    }
    return reasonPhrase;
  }

}
