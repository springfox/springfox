package com.mangofactory.swagger.plugins.operation;

import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.springmvc.plugins.OperationBuilderPlugin;
import com.mangofactory.springmvc.plugins.OperationContext;
import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OperationPositionReader implements OperationBuilderPlugin {

  private static final Logger log = LoggerFactory.getLogger(OperationPositionReader.class);

  @Override
  public void apply(OperationContext context) {
    ApiOperation apiOperation = context.getHandlerMethod().getMethodAnnotation(ApiOperation.class);
    if (null != apiOperation && apiOperation.position() > 0) {
      context.operationBuilder().position(apiOperation.position());
      log.debug("Added operation at position: {}", apiOperation.position());
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
