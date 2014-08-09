package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Math.*;

public class OperationPositionReader implements RequestMappingReader {

  private static final Logger log = LoggerFactory.getLogger(OperationPositionReader.class);

  @Override
  public void execute(RequestMappingContext context) {
    int origPosition = (Integer) context.get("currentCount");
    Integer operationPosition = origPosition;
    ApiOperation apiOperation = context.getApiOperationAnnotation();
    if (null != apiOperation && apiOperation.position() > 0) {
      operationPosition = apiOperation.position();
    }
    context.put("position", operationPosition);
    int next = max((origPosition + 1), operationPosition);
    context.put("currentCount", next);
    log.debug("Added operation at position: {}. Next position is: {}", operationPosition, next);
  }
}
