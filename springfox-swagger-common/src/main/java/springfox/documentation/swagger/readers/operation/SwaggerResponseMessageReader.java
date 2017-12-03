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
package springfox.documentation.swagger.readers.operation;

import static springfox.documentation.schema.ResolvedTypes.modelRefFactory;
import static springfox.documentation.spi.schema.contexts.ModelContext.returnValue;
import static springfox.documentation.spring.web.readers.operation.ResponseMessagesReader.httpStatusCode;
import static springfox.documentation.spring.web.readers.operation.ResponseMessagesReader.message;
import static springfox.documentation.swagger.annotations.Annotations.resolvedTypeFromOperation;
import static springfox.documentation.swagger.annotations.Annotations.resolvedTypeFromResponse;
import static springfox.documentation.swagger.readers.operation.ResponseHeaders.headers;
import static springfox.documentation.swagger.readers.operation.ResponseHeaders.responseHeaders;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.service.Header;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class SwaggerResponseMessageReader implements OperationBuilderPlugin {


  private final TypeNameExtractor typeNameExtractor;
  private final TypeResolver typeResolver;

  @Autowired
  public SwaggerResponseMessageReader(TypeNameExtractor typeNameExtractor, TypeResolver typeResolver) {
    this.typeNameExtractor = typeNameExtractor;
    this.typeResolver = typeResolver;
  }

  @Override
  public void apply(OperationContext context) {
    context.operationBuilder()
        .responseMessages(read(context));

  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }

  protected Set<ResponseMessage> read(OperationContext context) {
    ResolvedType defaultResponse = context.getReturnType();
    Optional<ApiOperation> operationAnnotation = context.findAnnotation(ApiOperation.class);
    Optional<ResolvedType> operationResponse =
        operationAnnotation.map(resolvedTypeFromOperation(typeResolver, defaultResponse));
    Optional<ResponseHeader[]> defaultResponseHeaders = operationAnnotation.map(responseHeaders());
    Map<String, Header> defaultHeaders = new HashMap<>();
    if (defaultResponseHeaders.isPresent()) {
      defaultHeaders.putAll(headers(defaultResponseHeaders.get()));
    }

    List<ApiResponses> allApiResponses = context.findAllAnnotations(ApiResponses.class);
    Set<ResponseMessage> responseMessages = new HashSet<>();

    Map<Integer, ApiResponse> seenResponsesByCode = new HashMap<>();
    for (ApiResponses apiResponses : allApiResponses) {
      ApiResponse[] apiResponseAnnotations = apiResponses.value();
      for (ApiResponse apiResponse : apiResponseAnnotations) {
        if (!seenResponsesByCode.containsKey(apiResponse.code())) {
          seenResponsesByCode.put(apiResponse.code(), apiResponse);
          ModelContext modelContext = returnValue(
              context.getGroupName(), apiResponse.response(),
              context.getDocumentationType(),
              context.getAlternateTypeProvider(),
              context.getGenericsNamingStrategy(),
              context.getIgnorableParameterTypes());
          Optional<ModelReference> responseModel = Optional.empty();
          Optional<ResolvedType> type = resolvedType(null, apiResponse);
          if (isSuccessful(apiResponse.code())) {
            type = type.isPresent() ? type : operationResponse;
          }
          if (type.isPresent()) {
            responseModel = Optional.of(
                modelRefFactory(modelContext, typeNameExtractor)
                    .apply(context.alternateFor(type.get())));
          }
          Map<String, Header> headers = new HashMap<>(defaultHeaders);
          headers.putAll(headers(apiResponse.responseHeaders()));

          responseMessages.add(new ResponseMessageBuilder()
              .code(apiResponse.code())
              .message(apiResponse.message())
              .responseModel(responseModel.orElse(null))
              .headersWithDescription(headers)
              .build());
        }
      }
    }
    if (operationResponse.isPresent()) {
      ModelContext modelContext = returnValue(
          context.getGroupName(),
          operationResponse.get(),
          context.getDocumentationType(),
          context.getAlternateTypeProvider(),
          context.getGenericsNamingStrategy(),
          context.getIgnorableParameterTypes());
      ResolvedType resolvedType = context.alternateFor(operationResponse.get());

      ModelReference responseModel = modelRefFactory(modelContext, typeNameExtractor).apply(resolvedType);
      context.operationBuilder().responseModel(responseModel);
      ResponseMessage defaultMessage = new ResponseMessageBuilder()
          .code(httpStatusCode(context))
          .message(message(context))
          .responseModel(responseModel)
          .build();
      if (!responseMessages.contains(defaultMessage) && !"void".equals(responseModel.getType())) {
        responseMessages.add(defaultMessage);
      }
    }
    return responseMessages;
  }


  static boolean isSuccessful(int code) {
    try {
      return HttpStatus.Series.SUCCESSFUL.equals(HttpStatus.Series.valueOf(code));
    } catch (Exception ignored) {
      return false;
    }
  }

  private Optional<ResolvedType> resolvedType(ResolvedType resolvedType, ApiResponse apiResponse) {
    return Optional.ofNullable(resolvedTypeFromResponse(typeResolver, resolvedType).apply(apiResponse));
  }

}
