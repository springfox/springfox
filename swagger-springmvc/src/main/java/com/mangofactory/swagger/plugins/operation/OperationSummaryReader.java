package com.mangofactory.swagger.plugins.operation;

import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.springmvc.plugins.OperationBuilderPlugin;
import com.mangofactory.springmvc.plugins.OperationContext;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class OperationSummaryReader implements OperationBuilderPlugin {

  @Override
  public void apply(OperationContext context) {
    ApiOperation apiOperationAnnotation = context.getHandlerMethod().getMethodAnnotation(ApiOperation.class);
    if (null != apiOperationAnnotation && StringUtils.hasText(apiOperationAnnotation.value())) {
      context.operationBuilder().summary(apiOperationAnnotation.value());
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
