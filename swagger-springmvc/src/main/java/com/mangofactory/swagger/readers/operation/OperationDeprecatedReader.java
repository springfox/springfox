package com.mangofactory.swagger.readers.operation;

import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.springmvc.plugins.OperationBuilderPlugin;
import com.mangofactory.springmvc.plugins.OperationContext;
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
