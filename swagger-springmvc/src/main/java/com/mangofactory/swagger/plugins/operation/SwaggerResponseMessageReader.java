package com.mangofactory.swagger.plugins.operation;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.service.model.ResponseMessage;
import com.mangofactory.service.model.builder.ResponseMessageBuilder;
import com.mangofactory.spring.web.plugins.OperationBuilderPlugin;
import com.mangofactory.spring.web.plugins.OperationContext;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.util.Set;

import static com.google.common.collect.Sets.*;
import static com.mangofactory.schema.ResolvedTypes.*;
import static com.mangofactory.swagger.annotations.Annotations.*;

@Component
public class SwaggerResponseMessageReader implements OperationBuilderPlugin {

  private final TypeResolver typeResolver;

  @Autowired
  public SwaggerResponseMessageReader(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }

  @Override
  public void apply(OperationContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    context.operationBuilder()
            .responseMessages(read(handlerMethod));

  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  protected Set<ResponseMessage> read(HandlerMethod handlerMethod) {
    Optional<ApiResponses> apiResponsesOptional = findApiResponsesAnnotations(handlerMethod.getMethod());
    Set<ResponseMessage> responseMessages = newHashSet();
    if (apiResponsesOptional.isPresent()) {
      ApiResponse[] apiResponseAnnotations = apiResponsesOptional.get().value();
      for (ApiResponse apiResponse : apiResponseAnnotations) {
        String overrideTypeName = overrideTypeName(apiResponse);

        responseMessages.add(new ResponseMessageBuilder()
                .code(apiResponse.code())
                .message(apiResponse.message())
                .responseModel(overrideTypeName)
                .build());
      }
    }
    return responseMessages;
  }


  private String overrideTypeName(ApiResponse apiResponse) {
    if (apiResponse.response() != null) {
      return typeName(typeResolver.resolve(apiResponse.response()));
    }
    return "";
  }

}
