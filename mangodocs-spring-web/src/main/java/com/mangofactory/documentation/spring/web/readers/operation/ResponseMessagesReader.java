package com.mangofactory.documentation.spring.web.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.mangofactory.documentation.schema.TypeNameExtractor;
import com.mangofactory.documentation.service.model.ResponseMessage;
import com.mangofactory.documentation.service.model.builder.ResponseMessageBuilder;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.OperationBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.OperationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;

import java.util.List;

import static com.google.common.base.Optional.*;
import static com.google.common.collect.Sets.*;
import static com.mangofactory.documentation.spi.schema.contexts.ModelContext.*;
import static com.mangofactory.documentation.spring.web.HandlerMethodReturnTypes.*;
import static org.springframework.core.annotation.AnnotationUtils.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ResponseMessagesReader implements OperationBuilderPlugin {

  private final TypeResolver typeResolver;
  private final TypeNameExtractor typeNameExtractor;

  @Autowired
  public ResponseMessagesReader(TypeResolver typeResolver,
                                TypeNameExtractor typeNameExtractor) {
    this.typeResolver = typeResolver;
    this.typeNameExtractor = typeNameExtractor;
  }

  @Override
  public void apply(OperationContext context) {
    List<ResponseMessage> responseMessages = context.getGlobalResponseMessages(context.httpMethod());
    context.operationBuilder().responseMessages(newHashSet(responseMessages));
    applyReturnTypeOverride(context);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private void applyReturnTypeOverride(OperationContext context) {

    ResolvedType returnType = handlerReturnType(typeResolver, context.getHandlerMethod());
    returnType = context.alternateFor(returnType);
    int httpStatusCode = httpStatusCode(context.getHandlerMethod());
    String message = HttpStatus.valueOf(httpStatusCode).getReasonPhrase();
    String simpleName = null;
    if (!Void.class.equals(returnType.getErasedType()) && !Void.TYPE.equals(returnType.getErasedType())) {
      simpleName = typeNameExtractor.typeName(returnValueWithoutContainerType(returnType,
              context.getDocumentationType(), context.getAlternateTypeProvider()));
    }
    ResponseMessage built = new ResponseMessageBuilder()
            .code(httpStatusCode)
            .message(message)
            .responseModel(simpleName)
            .build();
    context.operationBuilder().responseMessages(newHashSet(built));
  }

  private int httpStatusCode(HandlerMethod handlerMethod) {
    Optional<ResponseStatus> responseStatus
            = fromNullable(getAnnotation(handlerMethod.getMethod(), ResponseStatus.class));
    int httpStatusCode = 200;
    if (responseStatus.isPresent()) {
      httpStatusCode = responseStatus.get().value().value();
    }
    return httpStatusCode;
  }

}
