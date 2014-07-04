package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.model.Parameter;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class ImplicitParametersReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      HandlerMethod handlerMethod = context.getHandlerMethod();
      Method method = handlerMethod.getMethod();
      if (!method.isAnnotationPresent(ApiImplicitParams.class)) {
         return;
      }
      ApiImplicitParams annotation = method.getAnnotation(ApiImplicitParams.class);
      ImplicitParameterReader implicitParameter = new ImplicitParameterReader();
      List<Parameter> parameters = (List<Parameter>) context.get("parameters");
      if (parameters == null) {
         parameters = newArrayList();
      }
      for (ApiImplicitParam param: annotation.value()) {
         parameters.add(implicitParameter.getImplicitParameter(param));
      }
      context.put("parameters", parameters);
   }
}
