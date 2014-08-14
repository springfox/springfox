package com.mangofactory.swagger.readers.operation;

import com.google.common.base.Function;
import com.google.common.base.Optional;
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
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.model.AllowableListValues;
import com.wordnik.swagger.model.AllowableValues;
import com.wordnik.swagger.model.Parameter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.HandlerMethod;
import scala.collection.JavaConversions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Optional.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.ScalaUtils.*;
import static com.mangofactory.swagger.models.Types.*;
import static com.mangofactory.swagger.readers.operation.parameter.ParameterAllowableReader.*;
import static java.lang.reflect.Modifier.*;

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
    return parameters;
  }

  private void expandModelAttribute(final String parentName, final Class<?> paramType,
                                    final List<Parameter> parameters) {

    Field[] fields = paramType.getDeclaredFields();

    for (Field field : fields) {
      if (isStatic(field.getModifiers()) || field.isSynthetic()) {
        continue;
      }

      if (!typeBelongsToJavaPackage(field) && !field.getType().isEnum()) {

        expandModelAttribute(field.getName(), field.getType(), parameters);
        continue;
      }

      String dataTypeName = typeNameFor(field.getType());

      if (dataTypeName == null) {
        dataTypeName = field.getType().getSimpleName();
      }

      AllowableValues allowable;

      if (field.getAnnotation(ApiModelProperty.class) != null) {
        ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);

        String allowableProperty = emptyToNull(apiModelProperty.allowableValues());
        allowable = allowableValues(fromNullable(allowableProperty), field);

        Parameter annotatedModelParam = new Parameter(
                parentName != null ? new StringBuilder(parentName).append(".")
                        .append(field.getName()).toString() : field.getName(),
                toOption(apiModelProperty.value()),
                toOption(null), //default value
                apiModelProperty.required(),
                Boolean.FALSE,  //allow multiple
                dataTypeName,
                allowable,
                "query", //param type
                toOption(apiModelProperty.access())
        );

        parameters.add(annotatedModelParam);

      } else if (field.getAnnotation(ApiParam.class) != null) {
        ApiParam apiParam = field.getAnnotation(ApiParam.class);
        String allowableProperty = emptyToNull(apiParam.allowableValues());
        allowable = allowableValues(fromNullable(allowableProperty), field);

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

        allowable = allowableValues(Optional.<String>absent(), field);

        Parameter unannotatedParam = new Parameter(
                parentName != null ? new StringBuilder(parentName).append(".")
                        .append(field.getName()).toString() : field.getName(),
                toOption(null), //description
                toOption(null), //default value
                Boolean.FALSE,  //required
                Boolean.FALSE,  //allow multiple
                dataTypeName,   //data type
                allowable,           //allowable values
                "query",        //param type
                toOption(null)  //param access
        );

        parameters.add(unannotatedParam);
      }
    }

  }

  private boolean typeBelongsToJavaPackage(Field field) {
    return (field.getType().getPackage() == null || field.getType().getPackage().getName().startsWith("java"));
  }

  private AllowableValues allowableValues(final Optional<String> optionalAllowable, final Field field) {

    AllowableValues allowable = null;
    if (field.getType().isEnum()) {
      allowable = new AllowableListValues(JavaConversions.collectionAsScalaIterable(
              getEnumValues(field.getType())).toList(), "LIST");
    } else if (optionalAllowable.isPresent()) {
      allowable = allowableValueFromString(optionalAllowable.get());
    }

    return allowable;
  }

  private List<String> getEnumValues(final Class<?> subject) {
    return transform(Arrays.asList(subject.getEnumConstants()), new Function<Object, String>() {
      @Override
      public String apply(final Object input) {
        return input.toString();
      }
    });
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
