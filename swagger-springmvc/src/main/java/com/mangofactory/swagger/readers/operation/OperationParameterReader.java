package com.mangofactory.swagger.readers.operation;

import static com.google.common.collect.Lists.newArrayList;
import static com.mangofactory.swagger.ScalaUtils.toOption;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.mangofactory.swagger.annotations.BeanParam;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.models.Types;
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
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.model.AllowableValues;
import com.wordnik.swagger.model.Parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;

public class OperationParameterReader implements Command<RequestMappingContext> {

    private static final Logger log = LoggerFactory.getLogger(OperationParameterReader.class);

    @Override
    public void execute(final RequestMappingContext context) {
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

                CommandExecutor<Map<String, Object>, RequestMappingContext> commandExecutor =
                        new CommandExecutor<Map<String, Object>, RequestMappingContext>();

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

                    expandAndAddBeanParam(null, methodParameter.getResolvedParameterType().getErasedType(), parameters);
                }

            }
        }
        context.put("parameters", parameters);
    }

    private void expandAndAddBeanParam(final String parentName, final Class<?> paramType, final List<Parameter> parameters) {

        Field[] fields = paramType.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            if (!Types.isBaseType(Types.typeNameFor(field.getType())) && (field.getAnnotation(BeanParam.class) != null ||
                    field.getType().getAnnotation(BeanParam.class) != null)) {

                expandAndAddBeanParam(field.getName().toLowerCase(), field.getType(), parameters);
                continue;
            }

            Annotation ignoreAnnotation = field.getAnnotation(ApiIgnore.class);

            if (ignoreAnnotation != null) {
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

                Parameter parameter1 = new Parameter(
                        parentName != null ? new StringBuilder(parentName).append(".")
                                .append(field.getName()).toString() : field.getName(), //param name
                        toOption(apiParam.value()), //description
                        toOption(apiParam.defaultValue()), //default value
                        apiParam.required(), //required
                        apiParam.allowMultiple(), //allow multiple
                        dataTypeName, //data type
                        allowable, //Allowable values
                        "query", //param type
                        toOption(apiParam.access())
                        );

                parameters.add(parameter1);

            } else {

                Parameter parameter1 = new Parameter(
                        parentName != null ? new StringBuilder(parentName).append(".")
                                .append(field.getName()).toString() : field.getName(), //param name
                        toOption(null), //description
                        toOption(null), //default value
                        Boolean.FALSE, //required
                        Boolean.FALSE, //allow multiple
                        dataTypeName, //data type
                        null, //Allowable values
                        "query", //param type
                        toOption(null)
                        );

                parameters.add(parameter1);
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
                if (BeanParam.class == annotation.annotationType()) {
                    return true;
                }
            }

        return false;
    }
}
