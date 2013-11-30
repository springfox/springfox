package com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.*;

public class ParameterDataTypeReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      MethodParameter methodParameter = (MethodParameter) context.get("methodParameter");
      ApiParam apiParam = methodParameter.getParameterAnnotation(ApiParam.class);
      Class<?> parameterType = methodParameter.getParameterType();




      context.put("dataType", "none");

   }

   private String getParameterType(MethodParameter methodParameter, ResolvedType parameterType) {
      RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
      if (requestParam != null) {
         return "query";
      }
      PathVariable pathVariable = methodParameter.getParameterAnnotation(PathVariable.class);
      if (pathVariable != null) {
         return "path";
      }
      RequestBody requestBody = methodParameter.getParameterAnnotation(RequestBody.class);
      if (requestBody != null) {
         return "body";
      }
      ModelAttribute modelAttribute = methodParameter.getParameterAnnotation(ModelAttribute.class);
      if (modelAttribute != null) {
         return "body";
      }
      RequestHeader requestHeader = methodParameter.getParameterAnnotation(RequestHeader.class);
      if (requestHeader != null) {
         return "header";
      }
//      if (isPrimitive(parameterType.getErasedType())) {
//         return "query";
//      }
      return "body";
   }

}
