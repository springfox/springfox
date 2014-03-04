package com.mangofactory.swagger.readers.operation.parameter;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;

public class ParameterTypeReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      MethodParameter methodParameter = (MethodParameter) context.get("methodParameter");
      context.put("paramType", findParameterType(methodParameter));
   }

   private String findParameterType(MethodParameter methodParameter) {
      Annotation[] methodAnnotations = methodParameter.getParameterAnnotations();
      if (null != methodAnnotations) {
         for (Annotation annotation : methodAnnotations) {
            if (annotation instanceof PathVariable) {
               return "path";
            } else if (annotation instanceof ModelAttribute) {
               return "body";
            } else if (annotation instanceof RequestBody) {
               return "body";
            } else if (annotation instanceof RequestParam) {
               return "query";
            } else if (annotation instanceof RequestHeader) {
               return "header";
            }
         }
      }
      return "body";
   }
}
