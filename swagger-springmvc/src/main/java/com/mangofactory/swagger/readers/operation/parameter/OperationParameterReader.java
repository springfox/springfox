package com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.service.model.Parameter;
import com.mangofactory.service.model.builder.ParameterBuilder;
import com.mangofactory.springmvc.plugins.DocumentationPluginsManager;
import com.mangofactory.springmvc.plugins.OperationBuilderPlugin;
import com.mangofactory.springmvc.plugins.OperationContext;
import com.mangofactory.springmvc.plugins.ParameterContext;
import com.mangofactory.swagger.readers.operation.HandlerMethodResolver;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;

@Component
public class OperationParameterReader implements OperationBuilderPlugin {
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
  public void apply(OperationContext context) {
    context.operationBuilder().parameters(readParameters(context));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  protected List<Parameter> readParameters(final OperationContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    HandlerMethodResolver handlerMethodResolver = new HandlerMethodResolver(typeResolver);

    List<ResolvedMethodParameter> methodParameters = handlerMethodResolver.methodParameters(handlerMethod);
    List<Parameter> parameters = newArrayList();

    for (ResolvedMethodParameter methodParameter : methodParameters) {

      if (!shouldIgnore(methodParameter, context.getDocumentationContext().getIgnorableParameterTypes())) {

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
