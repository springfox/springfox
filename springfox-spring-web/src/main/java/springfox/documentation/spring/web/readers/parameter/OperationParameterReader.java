/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.spring.web.readers.parameter;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Lists.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
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

        ParameterContext parameterContext = new ParameterContext(methodParameter, new ParameterBuilder(),
                context.getDocumentationContext(), context.getDocumentationContext().getGenericsNamingStrategy());

        if (!shouldExpand(methodParameter)) {
          parameters.add(pluginsManager.parameter(parameterContext));
        } else {
          expander.expand("", methodParameter.getResolvedParameterType().getErasedType(), parameters,
                  context.getDocumentationContext());
        }
      }
    }
    return parameters;
  }

  private boolean shouldIgnore(final ResolvedMethodParameter parameter, final Set<Class> ignorableParamTypes) {
    if (ignorableParamTypes.contains(parameter.getMethodParameter().getParameterType())) {
      return true;
    }
    for (Annotation annotation : parameter.getMethodParameter().getParameterAnnotations()) {
      if (ignorableParamTypes.contains(annotation.annotationType())) {
        return true;
      }
    }
    return false;
  }

  private boolean shouldExpand(final ResolvedMethodParameter parameter) {
    return !from(newArrayList(parameter.getMethodParameter().getParameterAnnotations()))
            .filter(ModelAttribute.class).isEmpty();

  }
}
