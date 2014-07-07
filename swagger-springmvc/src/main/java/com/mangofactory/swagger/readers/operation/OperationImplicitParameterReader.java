package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.operation.parameter.ParameterAllowableReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.model.Parameter;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.mangofactory.swagger.ScalaUtils.*;

public class OperationImplicitParameterReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      HandlerMethod handlerMethod = context.getHandlerMethod();
      Method method = handlerMethod.getMethod();
      if (!method.isAnnotationPresent(ApiImplicitParam.class)) {
         return;
      }
      ApiImplicitParam annotation = method.getAnnotation(ApiImplicitParam.class);
      List<Parameter> parameters = (List<Parameter>) context.get("parameters");
      if (parameters == null) {
         parameters = newArrayList();
      }
      parameters.add(OperationImplicitParameterReader.getImplicitParameter(annotation));
      context.put("parameters", parameters);
   }

   public static Parameter getImplicitParameter(ApiImplicitParam param) {
      Parameter parameter = new Parameter(
              param.name(),
              toOption(param.value()),
              toOption(param.defaultValue()),
              param.required(),
              param.allowMultiple(),
              param.dataType(),
              ParameterAllowableReader.getAllowableValueFromString(param.allowableValues()),
              param.paramType(),
              toOption(param.access())
      );
      return parameter;
   }

}

