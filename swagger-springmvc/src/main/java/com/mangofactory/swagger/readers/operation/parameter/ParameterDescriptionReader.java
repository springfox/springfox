package com.mangofactory.swagger.readers.operation.parameter;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;

import static org.apache.commons.lang.StringUtils.isBlank;

public class ParameterDescriptionReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      MethodParameter methodParameter = (MethodParameter) context.get("methodParameter");
      ApiParam apiParam = methodParameter.getParameterAnnotation(ApiParam.class);
      String description = methodParameter.getParameterName();
      if(null != apiParam && !isBlank(apiParam.value())){
         description = apiParam.value();
      }
      context.put("description", description);
   }
}
