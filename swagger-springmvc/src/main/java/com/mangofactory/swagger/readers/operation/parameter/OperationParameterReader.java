package com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.service.model.Parameter;
import com.mangofactory.service.model.builder.ParameterBuilder;
import com.mangofactory.springmvc.plugins.DocumentationPluginsManager;
import com.mangofactory.springmvc.plugins.ParameterContext;
import com.mangofactory.swagger.readers.operation.HandlerMethodResolver;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;

@Component
public class OperationParameterReader implements RequestMappingReader {
  private final TypeResolver typeResolver;
  private final ModelAttributeParameterExpander expander;
  private final DocumentationPluginsManager pluginsManager;

  @Autowired
  public OperationParameterReader(TypeResolver typeResolver,
                                  ModelAttributeParameterExpander expander,
                                  DocumentationPluginsManager pluginsManager) {
    this.typeResolver = typeResolver;
    this.expander = expander;
    this.pluginsManager = pluginsManager;
  }

  @Override
  public final void execute(RequestMappingContext context) {
    List<Parameter> parameters = (List<Parameter>) context.get("parameters");
    if (parameters == null) {
      parameters = newArrayList();
    }
    parameters.addAll(this.readParameters(context));
    context.put("parameters", parameters);
  }

  protected Collection<? extends Parameter> readParameters(final RequestMappingContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    HandlerMethodResolver handlerMethodResolver = new HandlerMethodResolver(typeResolver);

    List<ResolvedMethodParameter> methodParameters = handlerMethodResolver.methodParameters(handlerMethod);
    List<Parameter> parameters = newArrayList();

    for (ResolvedMethodParameter methodParameter : methodParameters) {

      if (!shouldIgnore(methodParameter, context.getDocumentationContext().getIgnorableParameterTypes())) {

        RequestMappingContext parameterRMCContext = context.newCopyUsingHandlerMethod(handlerMethod);
        parameterRMCContext.put("methodParameter", methodParameter.getMethodParameter());
        parameterRMCContext.put("resolvedMethodParameter", methodParameter);

        ParameterContext parameterContext = new ParameterContext(methodParameter, new  ParameterBuilder(),
                context.getDocumentationContext());

        if (!shouldExpand(methodParameter)) {
          parameters.add(pluginsManager.parameter(parameterContext));
        } else {
          expander.expand("", methodParameter.getResolvedParameterType().getErasedType(), parameters, context
                  .getDocumentationContext().getDocumentationType());
        }
      }
    }
    return parameters;
  }

  private boolean shouldIgnore(final ResolvedMethodParameter parameter, final Set<Class> ignorableParamTypes) {
    if (!ignorableParamTypes.isEmpty()) {

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
