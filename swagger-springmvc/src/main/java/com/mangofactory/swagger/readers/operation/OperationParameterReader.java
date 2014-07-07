package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.operation.parameter.ParameterAllowableReader;
import com.mangofactory.swagger.readers.operation.parameter.ParameterDataTypeReader;
import com.mangofactory.swagger.readers.operation.parameter.ParameterDefaultReader;
import com.mangofactory.swagger.readers.operation.parameter.ParameterDescriptionReader;
import com.mangofactory.swagger.readers.operation.parameter.ParameterMultiplesReader;
import com.mangofactory.swagger.readers.operation.parameter.ParameterNameReader;
import com.mangofactory.swagger.readers.operation.parameter.ParameterRequiredReader;
import com.mangofactory.swagger.readers.operation.parameter.ParameterTypeReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.model.AllowableValues;
import com.wordnik.swagger.model.Parameter;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.ScalaUtils.*;

public class OperationParameterReader implements Command<RequestMappingContext> {
    @Override
    public void execute(RequestMappingContext context) {
        HandlerMethod handlerMethod = context.getHandlerMethod();
        SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");
        HandlerMethodResolver handlerMethodResolver
                = new HandlerMethodResolver(swaggerGlobalSettings.getTypeResolver());

        List<ResolvedMethodParameter> methodParameters = handlerMethodResolver.methodParameters(handlerMethod);
        List<Parameter> parameters = (List<Parameter>) context.get("parameters");
        if (parameters == null) {
           parameters = newArrayList();
        }
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

    private boolean shouldIgnore(ResolvedMethodParameter parameter, Set<Class> ignorableParamTypes) {
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
}
