package com.mangofactory.swagger.readers.operation.parameter;

import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.models.ModelProvider;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.operation.HandlerMethodResolver;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import com.mangofactory.swagger.readers.operation.SwaggerParameterReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.models.parameters.Parameter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

public class OperationParameterReader extends SwaggerParameterReader {

  @Override
  protected Collection<? extends Parameter> readParameters(final RequestMappingContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");
    HandlerMethodResolver handlerMethodResolver
            = new HandlerMethodResolver(swaggerGlobalSettings.getTypeResolver());
    AlternateTypeProvider alternateTypeProvider = swaggerGlobalSettings.getAlternateTypeProvider();

    ModelProvider modelProvider = (ModelProvider) context.get("modelProvider");
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

    ModelAttributeParameterExpander expander = new ModelAttributeParameterExpander(alternateTypeProvider);
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

        SwaggerParameterBuilder swaggerParameterBuilder = new SwaggerParameterBuilder();
        Parameter parameter = swaggerParameterBuilder
                .withType((String) result.get("paramType"))
                .withDescription((String) result.get("description"))
                .withName((String) result.get("name"))
                .withRequired((Boolean) result.get("required"))
                .withDataType((String) result.get("dataType"))
                .withModelProvider(modelProvider)
                .withMethodParameter(methodParameter)
                .build();

//        Model asInput = modelProvider.modelFor(ModelContext.inputParam(simpleType())).get();

//        if (!shouldExpand(methodParameter)) {
//          Parameter parameter = new Parameter(
//                  (String) result.get("name"),
//                  toOption(result.get("description")),
//                  toOption(result.get("defaultValue")),
//                  (Boolean) result.get("required"),
//                  (Boolean) result.get("allowMultiple"),
//                  (String) result.get("dataType"),
//                  (AllowableValues) result.get("allowableValues"),
//                  (String) result.get("paramType"),
//                  toOption(result.get("paramAccess"))
//          );
//          parameters.add(parameter);
//        } else {
//          expander.expand("", methodParameter.getResolvedParameterType().getErasedType(), parameters);
//        }

        parameters.add(parameter);
      }
    }
    return parameters;
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
