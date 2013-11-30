package com.mangofactory.swagger.readers.operation.parameter;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestParam;

public class ParameterRequiredReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      MethodParameter methodParameter = (MethodParameter) context.get("methodParameter");
      ApiParam apiParam = methodParameter.getParameterAnnotation(ApiParam.class);
      Boolean isRequired = true;
      if(null != apiParam){
         isRequired = apiParam.required();
      } else {
         RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
         if(null != requestParam){
            isRequired = requestParam.required();
         }
      }
      context.put("required", isRequired);
   }
}
