package com.mangofactory.swagger.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.operation.parameter.ParameterDescriptionReader;
import com.mangofactory.swagger.readers.operation.parameter.ParameterNameReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.model.AllowableValues;
import com.wordnik.swagger.model.Parameter;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.mangofactory.swagger.ScalaUtils.toOption;

public class OperationParameterReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      HandlerMethod handlerMethod = context.getHandlerMethod();
      Set<Class> ignorableParameterTypes = (Set<Class>) context.get("ignorableParameterTypes");

      MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
      List<Parameter> parameters = newArrayList();

      for (MethodParameter methodParameter : methodParameters) {
         Class<?> parameterType = methodParameter.getParameterType();
         if (!shouldIgnore(parameterType, ignorableParameterTypes)) {

            RequestMappingContext parameterContext = new RequestMappingContext(context.getRequestMappingInfo(), handlerMethod);
            parameterContext.put("methodParameter", methodParameter);

            CommandExecutor<Map<String, Object>, RequestMappingContext> commandExecutor = new CommandExecutor();
            List<Command<RequestMappingContext>> commandList = newArrayList();

            commandList.add(new ParameterNameReader());
            commandList.add(new ParameterDescriptionReader());

            commandExecutor.execute(commandList, parameterContext);

            Map<String, Object> result = parameterContext.getResult();
            Parameter parameter = new Parameter(
                  (String) result.get("name"),
                  toOption(result.get("description")),
                  toOption(result.get("defaultValue")),
                  (Boolean) result.get("required"),
                  (Boolean) result.get("allowMultiple"),
                  (String) result.get("dataType"),
                  (AllowableValues) context.get("allowableValues"),
                  (String) result.get("paramType"),
                  toOption(result.get("paramAccess"))
            );
            parameters.add(parameter);
            //Parameter (
//            name: String,
//            description: Option[String],
//            defaultValue: Option[String],
//            required: Boolean,
//            allowMultiple: Boolean,
//            dataType: String,
//            allowableValues: AllowableValues = AnyAllowableValues,
//            paramType: String,
//            paramAccess: Option[String] = None)


         }

      }


//
      context.put("parameters", parameters);
   }

   private boolean shouldIgnore(Class paramType, Set<Class> ignorabledParamTypes) {
      if (null != ignorabledParamTypes && !ignorabledParamTypes.isEmpty()) {
         if (ignorabledParamTypes.contains(paramType)) {
            return true;
         }
      }
      return false;
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
      if (isPrimitive(parameterType.getErasedType())) {
         return "query";
      }
      return "body";
   }

   public static boolean isPrimitive(Class<?> parameterType) {
      return parameterType.isPrimitive() ||
            String.class.isAssignableFrom(parameterType) ||
            Date.class.isAssignableFrom(parameterType) ||
            Byte.class.isAssignableFrom(parameterType) ||
            Boolean.class.isAssignableFrom(parameterType) ||
            Integer.class.isAssignableFrom(parameterType) ||
            Long.class.isAssignableFrom(parameterType) ||
            Float.class.isAssignableFrom(parameterType) ||
            Double.class.isAssignableFrom(parameterType);
   }
}
