package com.mangofactory.documentation.spring.web.readers.operation;

import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.OperationBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.OperationContext;
import org.springframework.stereotype.Component;

@Component
public class DefaultOperationBuilder implements OperationBuilderPlugin {
  @Override
  public void apply(OperationContext context) {
    String operationName = context.getHandlerMethod().getMethod().getName();
    context.operationBuilder()
            .method(context.httpMethod())
            .nickname(operationName)
            .notes(operationName)
            .position(context.operationIndex())
            .summary(operationName);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
