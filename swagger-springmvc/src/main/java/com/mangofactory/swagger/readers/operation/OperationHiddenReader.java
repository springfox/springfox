package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.method.HandlerMethod;

public class OperationHiddenReader implements RequestMappingReader {
  @Override
  public void execute(RequestMappingContext context) {

    HandlerMethod handlerMethod = context.getHandlerMethod();
    boolean isHidden = false;
    ApiOperation methodAnnotation = handlerMethod.getMethodAnnotation(ApiOperation.class);
    if ((null != methodAnnotation)) {
      isHidden = methodAnnotation.hidden();
    }
    context.put("isHidden", isHidden);
  }
}
