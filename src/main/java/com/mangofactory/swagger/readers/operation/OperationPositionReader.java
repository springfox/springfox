package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;

import static java.lang.Math.max;

public class OperationPositionReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      int origPosition = (Integer) context.get("currentCount");
      Integer operationPosition = origPosition;
      ApiOperation apiOperation = context.getApiOperationAnnotation();
      if (null != apiOperation && apiOperation.position() > 0) {
         operationPosition = apiOperation.position();
      }
      context.put("position", operationPosition);
      context.put("currentCount", max((origPosition + 1), operationPosition ));

   }
}
