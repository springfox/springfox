package com.mangofactory.swagger.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.schema.ResolvedTypes;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.service.model.ResponseMessage;
import com.mangofactory.service.model.builder.ResponseMessageBuilder;
import com.mangofactory.springmvc.plugins.OperationBuilderPlugin;
import com.mangofactory.springmvc.plugins.OperationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;

import java.util.List;

import static com.google.common.collect.Sets.*;
import static com.mangofactory.swagger.core.ModelUtils.*;

@Component
public class ResponseMessagesReader implements OperationBuilderPlugin {

  private final TypeResolver typeResolver;
  private final AlternateTypeProvider alternateTypeProvider;

  @Autowired
  public ResponseMessagesReader(TypeResolver typeResolver, AlternateTypeProvider alternateTypeProvider) {
    this.typeResolver = typeResolver;
    this.alternateTypeProvider = alternateTypeProvider;
  }

  @Override
  public void apply(OperationContext context) {
    List<ResponseMessage> responseMessages = context.getGlobalResponseMessages(context.httpMethod());
    context.operationBuilder()
            .responseMessages(newHashSet(responseMessages));

    applyReturnTypeOverride(context);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private void applyReturnTypeOverride(OperationContext context) {

    ResolvedType returnType = handlerReturnType(typeResolver, context.getHandlerMethod());
    returnType = alternateTypeProvider.alternateFor(returnType);
    int httpStatusCode = httpStatusCode(context.getHandlerMethod());
    String message = HttpStatus.valueOf(httpStatusCode).getReasonPhrase();
    String simpleName = null;
    if (!Void.class.equals(returnType.getErasedType()) && !Void.TYPE.equals(returnType.getErasedType())) {
      simpleName = ResolvedTypes.typeName(returnType);
    }
    ResponseMessage built = new ResponseMessageBuilder()
            .code(httpStatusCode)
            .message(message)
            .responseModel(simpleName)
            .build();
    context.operationBuilder().responseMessages(newHashSet(built));
  }

  private int httpStatusCode(HandlerMethod handlerMethod) {
    Optional<ResponseStatus> responseStatus = Optional.fromNullable(AnnotationUtils.getAnnotation(handlerMethod
            .getMethod(), ResponseStatus.class));
    int httpStatusCode = 200;
    if (responseStatus.isPresent()) {
      httpStatusCode = responseStatus.get().value().value();
    }
    return httpStatusCode;
  }

}
