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
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Set;

import static com.google.common.collect.Sets.*;
import static springfox.documentation.schema.ResolvedTypes.modelRefFactory;
import static springfox.documentation.spi.schema.contexts.ModelContext.*;
import static springfox.documentation.spring.web.readers.operation.ResponseMessagesReader.*;
import static springfox.documentation.swagger.annotations.Annotations.*;

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
    HandlerMethod handlerMethod = context.getHandlerMethod();
    context.operationBuilder()
        .responseMessages(read(handlerMethod, context));

  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }

  protected Set<ResponseMessage> read(HandlerMethod handlerMethod, OperationContext context) {
    ResolvedType defaultResponse = new HandlerMethodResolver(typeResolver).methodReturnType(handlerMethod);
    Optional<ResolvedType> operationResponse = findApiOperationAnnotation(handlerMethod.getMethod())
        .transform(resolvedTypeFromOperation(typeResolver, defaultResponse));
    Optional<ApiResponses> apiResponses = findApiResponsesAnnotations(handlerMethod.getMethod());
    Set<ResponseMessage> responseMessages = newHashSet();
    if (apiResponses.isPresent()) {
      ApiResponse[] apiResponseAnnotations = apiResponses.get().value();
      for (ApiResponse apiResponse : apiResponseAnnotations) {
        ModelContext modelContext = returnValue(apiResponse.response(), context.getDocumentationType(),
            context.getAlternateTypeProvider(), context.getDocumentationContext().getGenericsNamingStrategy());
        Optional<ModelReference> responseModel = Optional.absent();
        Optional<ResolvedType> type = resolvedType(null, apiResponse);
        if (isSuccessful(apiResponse.code())) {
          type = type.or(operationResponse);
        }
        if (type.isPresent()) {

          responseModel = Optional.of(modelRefFactory(modelContext, typeNameExtractor).apply(context.alternateFor(type.get())));
        }
        responseMessages.add(new ResponseMessageBuilder()
            .code(apiResponse.code())
            .message(apiResponse.message())
            .responseModel(responseModel.orNull())
            .build());
      }

    }
    if (operationResponse.isPresent()) {
      ModelContext modelContext = returnValue(operationResponse.get(),
          context.getDocumentationType(),
          context.getAlternateTypeProvider(),
          context.getDocumentationContext().getGenericsNamingStrategy());
      ResolvedType resolvedType = context.alternateFor(operationResponse.get());

      ModelReference responseModel = modelRefFactory(modelContext, typeNameExtractor).apply(resolvedType);
      context.operationBuilder().responseModel(responseModel);
      ResponseMessage defaultMessage = new ResponseMessageBuilder()
          .code(httpStatusCode(handlerMethod))
          .message(message(handlerMethod))
          .responseModel(responseModel)
          .build();
      if (!responseMessages.contains(defaultMessage) && !"void".equals(responseModel.getType())) {
        responseMessages.add(defaultMessage);
      }
    }
    return responseMessages;
  }

  static boolean isSuccessful(int code) {
    return HttpStatus.Series.SUCCESSFUL.equals(HttpStatus.Series.valueOf(code));
  }

  private Optional<ResolvedType> resolvedType(ResolvedType resolvedType, ApiResponse apiResponse) {
    return Optional.fromNullable(resolvedTypeFromResponse(typeResolver, resolvedType).apply(apiResponse));
  }

}
