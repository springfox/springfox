package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;

public class OperationPositionReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      Integer position = (Integer) context.get("currentCount");
      ApiOperation apiOperation = context.getApiOperationAnnotation();
      if (null != apiOperation && apiOperation.position() > 0) {
         position = apiOperation.position();
      }
      context.put("position", position);
   }
}
