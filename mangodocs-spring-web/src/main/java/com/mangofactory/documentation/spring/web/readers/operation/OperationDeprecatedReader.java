package com.mangofactory.documentation.spring.web.readers.operation;

import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.OperationBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.OperationContext;
import org.springframework.stereotype.Component;

@Component
public class OperationDeprecatedReader implements OperationBuilderPlugin {
  @Override
  public void apply(OperationContext context) {
    boolean isDeprecated = context.getHandlerMethod().getMethodAnnotation(Deprecated.class) != null;
    context.operationBuilder().deprecated(String.valueOf(isDeprecated));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
