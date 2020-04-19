/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
import io.swagger.annotations.ExampleProperty;
import io.swagger.annotations.ResponseHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ExampleBuilder;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.common.Compatibility;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.schema.property.ModelSpecificationFactory;
import springfox.documentation.service.Header;
import springfox.documentation.service.MediaType;
import springfox.documentation.service.Response;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ResponseContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.*;
import static org.springframework.util.StringUtils.*;
import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.spring.web.readers.operation.ResponseMessagesReader.*;
import static springfox.documentation.swagger.annotations.Annotations.*;
import static springfox.documentation.swagger.readers.operation.ResponseHeaders.*;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class SwaggerResponseMessageReader implements OperationBuilderPlugin {

  private final EnumTypeDeterminer enumTypeDeterminer;
  private final TypeNameExtractor typeNameExtractor;
  private final TypeResolver typeResolver;
  private final ModelSpecificationFactory modelSpecifications;
  private final DocumentationPluginsManager documentationPlugins;

  @Autowired
  public SwaggerResponseMessageReader(
      EnumTypeDeterminer enumTypeDeterminer,
      TypeNameExtractor typeNameExtractor,
      TypeResolver typeResolver,
      ModelSpecificationFactory modelSpecifications,
      DocumentationPluginsManager documentationPlugins) {
    this.enumTypeDeterminer = enumTypeDeterminer;
    this.typeNameExtractor = typeNameExtractor;
    this.typeResolver = typeResolver;
    this.modelSpecifications = modelSpecifications;
    this.documentationPlugins = documentationPlugins;
  }

  @Override
  public void apply(OperationContext context) {
    Compatibility<Set<ResponseMessage>, Set<Response>> read = read(context);
    context.operationBuilder().responseMessages(read.getLegacy().orElse(new HashSet<>()));
    context.operationBuilder().responses(read.getModern().orElse(new HashSet<>()));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }


  @SuppressWarnings({"CyclomaticComplexity", "NPathComplexity"})
  protected Compatibility<Set<ResponseMessage>, Set<Response>> read(OperationContext context) {
    ResolvedType defaultResponse = context.getReturnType();
    Optional<ApiOperation> operationAnnotation = context.findAnnotation(ApiOperation.class);
    Optional<ResolvedType> operationResponse =
        operationAnnotation.map(resolvedTypeFromOperation(
            typeResolver,
            defaultResponse));
    Optional<ResponseHeader[]> defaultResponseHeaders = operationAnnotation.map(ApiOperation::responseHeaders);
    Map<String, Header> defaultHeaders = new HashMap<>();
    defaultResponseHeaders.ifPresent(responseHeaders -> defaultHeaders.putAll(headers(responseHeaders)));


    List<ApiResponses> allApiResponses = context.findAllAnnotations(ApiResponses.class);
    Set<ResponseMessage> responseMessages = new HashSet<>();
    Set<Response> responses = new HashSet<>();

    Map<Integer, ApiResponse> seenResponsesByCode = new HashMap<>();
    for (ApiResponses apiResponses : allApiResponses) {
      ApiResponse[] apiResponseAnnotations = apiResponses.value();
      for (ApiResponse apiResponse : apiResponseAnnotations) {
        if (!seenResponsesByCode.containsKey(apiResponse.code())) {
          seenResponsesByCode.put(
              apiResponse.code(),
              apiResponse);
          Optional<ModelReference> responseModel = empty();
          ModelContext modelContext = context.operationModelsBuilder()
              .addReturn(
                  typeResolver.resolve(apiResponse.response()),
                  Optional.empty());
          Optional<ResolvedType> type = resolvedType(apiResponse);
          if (isSuccessful(apiResponse.code())) {
            type = type.map(Optional::of).orElse(operationResponse);
          }
          if (type.isPresent()) {
            final Map<String, String> knownNames = new HashMap<>();
            Optional.ofNullable(context.getKnownModels().get(modelContext.getParameterId()))
                .orElse(new HashSet<>())
                .forEach(model -> knownNames.put(
                    model.getId(),
                    model.getName()));

            responseModel = Optional.of(
                modelRefFactory(
                    modelContext,
                    enumTypeDeterminer,
                    typeNameExtractor,
                    knownNames)
                    .apply(context.alternateFor(type.get())));
          }
          List<Example> examples = new ArrayList<>();
          for (ExampleProperty exampleProperty : apiResponse.examples().value()) {
            if (!isEmpty(exampleProperty.value())) {
              final String mediaType = isEmpty(exampleProperty.mediaType()) ? null : exampleProperty.mediaType();
              examples.add(new ExampleBuilder().withMediaType(mediaType).withValue(exampleProperty.value()).build());
            }
          }
          Map<String, Header> headers = new HashMap<>(defaultHeaders);
          headers.putAll(headers(apiResponse.responseHeaders()));

          responseMessages.add(new ResponseMessageBuilder()
              .code(apiResponse.code())
              .message(apiResponse.message())
              .responseModel(responseModel.orElse(null))
              .examples(examples)
              .headersWithDescription(headers)
              .build());
          ResponseContext responseContext = new ResponseContext(
              type.orElse(null),
              context.getDocumentationContext(),
              context.getGenericsNamingStrategy(),
              context);
          Set<MediaType> mediaTypes = new HashSet<>();
          Optional<ResolvedType> finalType = type;
          context.consumes()
              .forEach(mediaType ->
                  mediaTypes.add(
                      new MediaType(
                          mediaType,
                          finalType.map(t -> modelSpecifications.create(modelContext, t))
                              .orElse(null),
                          new ArrayList<>(),
                          new ArrayList<>(),
                          new ArrayList<>())));
          responseContext.responseBuilder()
              .mediaTypes(mediaTypes)
              .examples(examples)
              .description(apiResponse.message())
              .code(String.valueOf(apiResponse.code()));
          responses.add(documentationPlugins.response(responseContext));
        }
      }
    }
    if (operationResponse.isPresent()) {
      ModelContext modelContext = context.operationModelsBuilder().addReturn(
          operationResponse.get(),
          Optional.empty());
      ResolvedType resolvedType = context.alternateFor(operationResponse.get());

      Map<String, String> knownNames = new HashMap<>();
      Optional.ofNullable(context.getKnownModels().get(modelContext.getParameterId()))
          .orElse(new HashSet<>())
          .forEach(model -> knownNames.put(
              model.getId(),
              model.getName()));

      ModelReference responseModel = modelRefFactory(
          modelContext,
          enumTypeDeterminer,
          typeNameExtractor,
          knownNames)
          .apply(resolvedType);
      context.operationBuilder().responseModel(responseModel);
      ResponseMessage defaultMessage = new ResponseMessageBuilder()
          .code(httpStatusCode(context))
          .message(message(context))
          .responseModel(responseModel)
          .build();
      if (!responseMessages.contains(defaultMessage) && !"void".equals(responseModel.getType())) {
        responseMessages.add(defaultMessage);
      }

      ResponseContext responseContext = new ResponseContext(
          resolvedType,
          context.getDocumentationContext(),
          context.getGenericsNamingStrategy(),
          context);
      Set<MediaType> mediaTypes = new HashSet<>();
      context.consumes()
          .forEach(mediaType ->
              mediaTypes.add(
                  new MediaType(
                      mediaType,
                      modelSpecifications.create(modelContext, resolvedType),
                      new ArrayList<>(),
                      new ArrayList<>(),
                      new ArrayList<>())));

      responseContext.responseBuilder()
          .mediaTypes(mediaTypes)
          .description(message(context))
          .code(String.valueOf(httpStatusCode(context)));
      responses.add(documentationPlugins.response(responseContext));
    }
    return new Compatibility<>(responseMessages, responses);
  }

  static boolean isSuccessful(int code) {
    try {
      return HttpStatus.Series.SUCCESSFUL.equals(HttpStatus.Series.valueOf(code));
    } catch (Exception ignored) {
      return false;
    }
  }

  private Optional<ResolvedType> resolvedType(
      ApiResponse apiResponse) {
    return ofNullable(resolvedTypeFromResponse(
        typeResolver,
        null).apply(apiResponse));
  }
}
