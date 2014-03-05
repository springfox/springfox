package com.mangofactory.swagger.readers.operation.parameter;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;

public class ParameterRequiredReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      MethodParameter methodParameter = (MethodParameter) context.get("methodParameter");
      Boolean isRequired = true;
      isRequired = getAnnotatedRequired(methodParameter);
      if (null == isRequired) {
         isRequired = true;
      }
      context.put("required", isRequired);
   }

   private Boolean getAnnotatedRequired(MethodParameter methodParameter) {
      Annotation[] methodAnnotations = methodParameter.getParameterAnnotations();
      if (null != methodAnnotations) {
    	// Default to false, then if any of the multiple parameters mark it as requried then make it true
         boolean returnValue = false; 
         for (Annotation annotation : methodAnnotations) {
            //Todo - APIParam annotation - required defaults to false. Maybe makes more
            // sense to default to true like springs @RequestParam
            if (annotation instanceof ApiParam) {
               returnValue |= ((ApiParam) annotation).required();
            } else if (annotation instanceof PathVariable) {
               // PathVariables are always required by their nature
               returnValue = true;
            } else if (annotation instanceof RequestParam) {
               returnValue |= ((RequestParam) annotation).required();
            } else if (annotation instanceof RequestHeader) {
               returnValue |= ((RequestHeader) annotation).required();
            }
         }
         return returnValue;
      }
      return null;
   }
}
