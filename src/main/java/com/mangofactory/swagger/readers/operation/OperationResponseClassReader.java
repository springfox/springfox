package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.springframework.web.method.HandlerMethod;

import static com.mangofactory.swagger.core.ModelUtils.getHandlerReturnType;

public class OperationResponseClassReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      HandlerMethod handlerMethod = context.getHandlerMethod();
      Class<?> returnType = getHandlerReturnType(handlerMethod);
      if (Void.class != returnType) {
         context.put("responseClass", returnType.getCanonicalName());
      }
   }
}
