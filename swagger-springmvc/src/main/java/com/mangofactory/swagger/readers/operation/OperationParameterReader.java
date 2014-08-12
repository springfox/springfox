package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.models.Types;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.model.AllowableValues;
import com.wordnik.swagger.model.Parameter;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.ScalaUtils.*;

public class OperationParameterReader extends SwaggerParameterReader {

  @Override
  protected Collection<? extends Parameter> readParameters(final RequestMappingContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");
    HandlerMethodResolver handlerMethodResolver
            = new HandlerMethodResolver(swaggerGlobalSettings.getTypeResolver());

    List<ResolvedMethodParameter> methodParameters = handlerMethodResolver.methodParameters(handlerMethod);
    List<Parameter> parameters = newArrayList();

    List<Command<RequestMappingContext>> commandList = newArrayList();
    commandList.add(new ParameterAllowableReader());
    commandList.add(new ParameterDataTypeReader());
    commandList.add(new ParameterTypeReader());
    commandList.add(new ParameterDefaultReader());
    commandList.add(new ParameterDescriptionReader());
    commandList.add(new ParameterMultiplesReader());
    commandList.add(new ParameterNameReader());
    commandList.add(new ParameterRequiredReader());
    for (ResolvedMethodParameter methodParameter : methodParameters) {

      if (!shouldIgnore(methodParameter, swaggerGlobalSettings.getIgnorableParameterTypes())) {

        RequestMappingContext parameterContext
                = new RequestMappingContext(context.getRequestMappingInfo(), handlerMethod);

        parameterContext.put("methodParameter", methodParameter.getMethodParameter());
        parameterContext.put("resolvedMethodParameter", methodParameter);
        parameterContext.put("swaggerGlobalSettings", swaggerGlobalSettings);

        CommandExecutor<Map<String, Object>, RequestMappingContext> commandExecutor = new CommandExecutor();

        commandExecutor.execute(commandList, parameterContext);

        Map<String, Object> result = parameterContext.getResult();

        if (!shouldExpand(methodParameter)) {
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

        } else {
            expandModelAttribute(null, methodParameter.getResolvedParameterType().getErasedType(), parameters);
        }
      }
    }
   return  parameters;
  }

  private void expandModelAttribute(final String parentName, final Class<?> paramType, final List<Parameter> parameters) {

      Field[] fields = paramType.getDeclaredFields();

      for (int i = 0; i < fields.length; i++) {
          Field field = fields[i];

          if (field.isSynthetic()) {
              continue;
          }

          if (field.getType().getPackage() != null &&
                  !field.getType().getPackage().getName().startsWith("java") && !field.getType().isEnum()) {

              expandModelAttribute(field.getName(), field.getType(), parameters);
              continue;
          }

          Annotation annotation = field.getAnnotation(ApiParam.class);

          String dataTypeName = Types.typeNameFor(field.getType());

          if (dataTypeName == null) {
              dataTypeName = field.getType().getSimpleName();
          }

          if (annotation instanceof ApiParam) {
              ApiParam apiParam = (ApiParam) annotation;

              AllowableValues allowable = null;
              if (apiParam.allowableValues() != null) {

                  allowable = ParameterAllowableReader.getAllowableValueFromString(apiParam.allowableValues());
              }

              Parameter annotatedParam = new Parameter(
                      parentName != null ? new StringBuilder(parentName).append(".")
                              .append(field.getName()).toString() : field.getName(),
                      toOption(apiParam.value()),
                      toOption(apiParam.defaultValue()),
                      apiParam.required(),
                      apiParam.allowMultiple(),
                      dataTypeName,
                      allowable,
                      "query", //param type
                      toOption(apiParam.access())
                      );

              parameters.add(annotatedParam);

          } else {

              Parameter unannotatedParam = new Parameter(
                      parentName != null ? new StringBuilder(parentName).append(".")
                              .append(field.getName()).toString() : field.getName(),
                      toOption(null), //description
                      toOption(null), //default value
                      Boolean.FALSE,  //required
                      Boolean.FALSE,  //allow multiple
                      dataTypeName,   //data type
                      null,           //allowable values
                      "query",        //param type
                      toOption(null)  //param access
                      );

              parameters.add(unannotatedParam);
          }
      }

  }

  private boolean shouldIgnore(final ResolvedMethodParameter parameter, final Set<Class> ignorableParamTypes) {
        if (null != ignorableParamTypes && !ignorableParamTypes.isEmpty()) {

            if (ignorableParamTypes.contains(parameter.getMethodParameter().getParameterType())) {
                return true;
            }
            for (Annotation annotation : parameter.getMethodParameter().getParameterAnnotations()) {
                if (ignorableParamTypes.contains(annotation.annotationType())) {
                    return true;
                }
            }
        }
        return false;
    }

  private boolean shouldExpand(final ResolvedMethodParameter parameter) {

        for (Annotation annotation : parameter.getMethodParameter().getParameterAnnotations()) {
            if (ModelAttribute.class == annotation.annotationType()) {
                return true;
            }
        }

        return false;
  }
}
