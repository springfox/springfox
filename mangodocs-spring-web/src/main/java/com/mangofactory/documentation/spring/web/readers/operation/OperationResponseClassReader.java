package com.mangofactory.documentation.spring.web.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.documentation.schema.TypeNameExtractor;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.OperationBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.OperationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import static com.mangofactory.documentation.spi.schema.contexts.ModelContext.*;
import static com.mangofactory.documentation.spring.web.HandlerMethodReturnTypes.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OperationResponseClassReader implements OperationBuilderPlugin {
  private static Logger log = LoggerFactory.getLogger(OperationResponseClassReader.class);
  private final TypeResolver typeResolver;
  private final TypeNameExtractor nameExtractor;

  @Autowired
  public OperationResponseClassReader(TypeResolver typeResolver,
                                      TypeNameExtractor nameExtractor) {
    this.typeResolver = typeResolver;
    this.nameExtractor = nameExtractor;
  }

  @Override
  public void apply(OperationContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    ResolvedType returnType = handlerReturnType(typeResolver, handlerMethod);
    returnType = context.alternateFor(returnType);
    String responseTypeName = nameExtractor.typeName(returnValue(returnType, context.getDocumentationType(),
            context.getAlternateTypeProvider()));
    log.debug("Setting spring response class to:" + responseTypeName);
    context.operationBuilder().responseClass(responseTypeName);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
