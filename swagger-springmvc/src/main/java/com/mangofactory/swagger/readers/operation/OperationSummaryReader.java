package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

public class OperationSummaryReader implements RequestMappingReader {
  @Override
  public void execute(RequestMappingContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    ApiOperation apiOperationAnnotation = context.getApiOperationAnnotation();

    String summary = handlerMethod.getMethod().getName();
    if (null != apiOperationAnnotation && StringUtils.hasText(apiOperationAnnotation.value())) {
      summary = apiOperationAnnotation.value();
    }
    context.put("summary", summary);
  }


}
