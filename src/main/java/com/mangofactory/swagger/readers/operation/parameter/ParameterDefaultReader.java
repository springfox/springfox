package com.mangofactory.swagger.readers.operation.parameter;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestParam;

import static org.apache.commons.lang.StringUtils.isBlank;

public class ParameterDefaultReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      MethodParameter methodParameter = (MethodParameter) context.get("methodParameter");
      ApiParam apiParam = methodParameter.getParameterAnnotation(ApiParam.class);
      String defaultValue = "";
      if (null != apiParam && !isBlank(apiParam.defaultValue())) {
         defaultValue = apiParam.defaultValue();
      } else {
         RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
         if (null != requestParam && !isBlank(requestParam.defaultValue())) {
            defaultValue = requestParam.defaultValue();
         }
      }
      context.put("defaultValue", defaultValue);
   }
}
