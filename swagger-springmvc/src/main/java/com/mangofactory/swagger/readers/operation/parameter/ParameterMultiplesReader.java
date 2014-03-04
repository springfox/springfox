package com.mangofactory.swagger.readers.operation.parameter;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;

public class ParameterMultiplesReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      MethodParameter methodParameter = (MethodParameter) context.get("methodParameter");
      ApiParam apiParam = methodParameter.getParameterAnnotation(ApiParam.class);

      Boolean allowMultiple = Boolean.FALSE;
      if (null != apiParam) {
         allowMultiple = apiParam.allowMultiple();
      } else {
         Class<?> parameterType = methodParameter.getParameterType();
         allowMultiple = parameterType.isArray()
               || Iterable.class.isAssignableFrom(parameterType);
      }
      context.put("allowMultiple", allowMultiple);
   }
}
