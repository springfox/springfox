package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.operation.parameter.*;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.model.AllowableValues;
import com.wordnik.swagger.model.Parameter;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.mangofactory.swagger.ScalaUtils.toOption;

public class OperationParameterReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      HandlerMethod handlerMethod = context.getHandlerMethod();
      SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings)context.get("swaggerGlobalSettings");

      MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
      List<Parameter> parameters = newArrayList();

      for (MethodParameter methodParameter : methodParameters) {

         if (!shouldIgnore(methodParameter, swaggerGlobalSettings.getIgnorableParameterTypes())) {

            RequestMappingContext parameterContext = new RequestMappingContext(context.getRequestMappingInfo(), handlerMethod);
            methodParameter.initParameterNameDiscovery(new LocalVariableTableParameterNameDiscoverer());

            parameterContext.put("methodParameter", methodParameter);
            parameterContext.put("swaggerGlobalSettings", swaggerGlobalSettings);

            CommandExecutor<Map<String, Object>, RequestMappingContext> commandExecutor = new CommandExecutor();
            List<Command<RequestMappingContext>> commandList = newArrayList();

            commandList.add(new ParameterAllowableReader());
            commandList.add(new ParameterDataTypeReader());
            commandList.add(new ParameterTypeReader());
            commandList.add(new ParameterDefaultReader());
            commandList.add(new ParameterDescriptionReader());
            commandList.add(new ParameterMultiplesReader());
            commandList.add(new ParameterNameReader());
            commandList.add(new ParameterRequiredReader());

            commandExecutor.execute(commandList, parameterContext);

            Map<String, Object> result = parameterContext.getResult();
            Parameter parameter = new Parameter(
                  (String) result.get("name"),
                  toOption(result.get("description")),
                  toOption(result.get("defaultValue")),
                  (Boolean) result.get("required"),
                  (Boolean) result.get("allowMultiple"),
                  (String) result.get("dataType"),
                  (AllowableValues) result.get("allowableValues"),
                  (String) result.get("paramType"),
                  toOption(result.get("paramAccess"))
            );
            parameters.add(parameter);
         }
      }
      context.put("parameters", parameters);
   }

   private boolean shouldIgnore(MethodParameter parameter, Set<Class> ignorableParamTypes) {
      if (null != ignorableParamTypes && !ignorableParamTypes.isEmpty()) {

         if (ignorableParamTypes.contains( parameter.getParameterType() )) {
            return true;
         }
         for (Annotation annotation : parameter.getParameterAnnotations()) {
             if(ignorableParamTypes.contains( annotation.annotationType() )) {
                 return true;
             }
         }
      }
      return false;
   }
}
