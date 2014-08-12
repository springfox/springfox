package com.mangofactory.swagger.readers.operation.parameter;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.Annotation;

import static org.apache.commons.lang.StringUtils.isBlank;

public class ParameterDefaultReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      MethodParameter methodParameter = (MethodParameter) context.get("methodParameter");
      String defaultValue = findAnnotatedDefaultValue(methodParameter);
      boolean isNull = defaultValue == null || defaultValue.equals(ValueConstants.DEFAULT_NONE);
      context.put("defaultValue", isNull ? "" : defaultValue);
   }

   private String findAnnotatedDefaultValue(MethodParameter methodParameter) {
      Annotation[] methodAnnotations = methodParameter.getParameterAnnotations();
      if (null != methodAnnotations) {
         for (Annotation annotation : methodAnnotations) {
            if (annotation instanceof ApiParam && !isBlank(((ApiParam) annotation).defaultValue())) {
               return ((ApiParam) annotation).defaultValue();
            } else if (annotation instanceof RequestParam) {
               return ((RequestParam) annotation).defaultValue();
            } else if (annotation instanceof RequestHeader) {
               return ((RequestHeader) annotation).defaultValue();
            }
         }
      }
      return null;
   }
}
