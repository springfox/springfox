package com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.service.model.AllowableValues;
import com.mangofactory.service.model.Parameter;
import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.operation.HandlerMethodResolver;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import com.mangofactory.swagger.readers.operation.SwaggerParameterReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.*;

@Component
public class OperationParameterReader extends SwaggerParameterReader {
  private final TypeResolver typeResolver;
  private final AlternateTypeProvider alternateTypeProvider;
  private final ParameterDataTypeReader parameterDataTypeReader;
  private final ParameterTypeReader parameterTypeReader;

  @Autowired
  public OperationParameterReader(TypeResolver typeResolver, AlternateTypeProvider alternateTypeProvider,
                                  ParameterDataTypeReader parameterDataTypeReader, ParameterTypeReader
          parameterTypeReader) {
    this.typeResolver = typeResolver;
    this.alternateTypeProvider = alternateTypeProvider;
    this.parameterDataTypeReader = parameterDataTypeReader;
    this.parameterTypeReader = parameterTypeReader;
  }

  @Override
  protected Collection<? extends Parameter> readParameters(final RequestMappingContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    HandlerMethodResolver handlerMethodResolver = new HandlerMethodResolver(typeResolver);

    List<ResolvedMethodParameter> methodParameters = handlerMethodResolver.methodParameters(handlerMethod);
    List<Parameter> parameters = newArrayList();

    List<Command<RequestMappingContext>> commandList = newArrayList();
    commandList.add(new ParameterAllowableReader());
    commandList.add(parameterDataTypeReader);
    commandList.add(parameterTypeReader);
    commandList.add(new ParameterDefaultReader());
    commandList.add(new ParameterDescriptionReader());
    commandList.add(new ParameterMultiplesReader());
    commandList.add(new ParameterNameReader());
    commandList.add(new ParameterRequiredReader());

    ModelAttributeParameterExpander expander = new ModelAttributeParameterExpander(alternateTypeProvider);
    for (ResolvedMethodParameter methodParameter : methodParameters) {

      if (!shouldIgnore(methodParameter, context.getDocumentationContext().getIgnorableParameterTypes())) {

        RequestMappingContext parameterContext = context.newCopyUsingHandlerMethod(handlerMethod);

        parameterContext.put("methodParameter", methodParameter.getMethodParameter());
        parameterContext.put("resolvedMethodParameter", methodParameter);

        CommandExecutor<Map<String, Object>, RequestMappingContext> commandExecutor = new CommandExecutor();

        commandExecutor.execute(commandList, parameterContext);

        Map<String, Object> result = parameterContext.getResult();

        if (!shouldExpand(methodParameter)) {
          Parameter parameter = new com.mangofactory.service.model.builder.ParameterBuilder()
                  .name((String) result.get("name"))
                  .description((String) result.get("description"))
                  .defaultValue((String) result.get("defaultValue"))
                  .required((Boolean) result.get("required"))
                  .allowMultiple((Boolean) result.get("allowMultiple"))
                  .dataType((String) result.get("dataType"))
                  .allowableValues((AllowableValues) result.get("allowableValues"))
                  .parameterType((String) result.get("paramType"))
                  .parameterAccess((String) result.get("paramAccess"))
                  .build();
          parameters.add(parameter);
        } else {
          expander.expand("", methodParameter.getResolvedParameterType().getErasedType(), parameters);
        }
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
