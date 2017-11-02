/*
 *
 *  Copyright 2015-2016 the original author or authors.
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

package springfox.documentation.spring.web.readers.operation;

import static springfox.documentation.schema.Collections.isContainerType;
import static springfox.documentation.schema.Maps.isMapType;
import static springfox.documentation.schema.Types.isBaseType;
import static springfox.documentation.schema.Types.typeNameFor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;

import com.fasterxml.classmate.ResolvedType;

import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.spring.web.readers.parameter.ExpansionContext;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OperationParameterReader implements OperationBuilderPlugin {
  private final ModelAttributeParameterExpander expander;
  private final EnumTypeDeterminer enumTypeDeterminer;

  @Autowired
  private DocumentationPluginsManager pluginsManager;

  @Autowired
  public OperationParameterReader(
      ModelAttributeParameterExpander expander,
      EnumTypeDeterminer enumTypeDeterminer) {
    this.expander = expander;
    this.enumTypeDeterminer = enumTypeDeterminer;
  }

  @Override
  public void apply(OperationContext context) {
    context.operationBuilder().parameters(context.getGlobalOperationParameters());
    context.operationBuilder().parameters(readParameters(context));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private List<Parameter> readParameters(final OperationContext context) {

    List<ResolvedMethodParameter> methodParameters = context.getParameters();
    List<Parameter> parameters = new ArrayList<>();

    for (ResolvedMethodParameter methodParameter : methodParameters) {
      ResolvedType alternate = context.alternateFor(methodParameter.getParameterType());
      if (!shouldIgnore(methodParameter, alternate, context.getIgnorableParameterTypes())) {

        ParameterContext parameterContext = new ParameterContext(methodParameter,
            new ParameterBuilder(),
            context.getDocumentationContext(),
            context.getGenericsNamingStrategy(),
            context);

        if (shouldExpand(methodParameter, alternate)) {
          parameters.addAll(
              expander.expand(
                  new ExpansionContext("", methodParameter.getParameterType(), context.getDocumentationContext())));
        } else {
          parameters.add(pluginsManager.parameter(parameterContext));
        }
      }
    }
    return parameters.stream().filter(hiddenParams().negate()).collect(Collectors.toList());
  }

  private Predicate<Parameter> hiddenParams() {
    return new Predicate<Parameter>() {
      @Override
      public boolean test(Parameter input) {
        return input.isHidden();
      }
    };
  }

  private boolean shouldIgnore(
      final ResolvedMethodParameter parameter,
      ResolvedType resolvedParameterType,
      final Set<Class> ignorableParamTypes) {

    if (ignorableParamTypes.contains(resolvedParameterType.getErasedType())) {
      return true;
    }
    return ignorableParamTypes.stream()
        .filter(isAnnotation())
        .filter(parameterIsAnnotatedWithIt(parameter)).count() > 0;

  }

  private Predicate<Class> parameterIsAnnotatedWithIt(final ResolvedMethodParameter parameter) {
    return new Predicate<Class>() {
      @Override
      public boolean test(Class input) {
        return parameter.hasParameterAnnotation(input);
      }
    };
  }

  private Predicate<Class> isAnnotation() {
    return new Predicate<Class>() {
      @Override
      public boolean test(Class input) {
        return Annotation.class.isAssignableFrom(input);
      }
    };
  }

  private boolean shouldExpand(final ResolvedMethodParameter parameter, ResolvedType resolvedParamType) {
    return !parameter.hasParameterAnnotation(RequestBody.class)
        && !parameter.hasParameterAnnotation(RequestPart.class)
        && !isBaseType(typeNameFor(resolvedParamType.getErasedType()))
        && !enumTypeDeterminer.isEnum(resolvedParamType.getErasedType())
        && !isContainerType(resolvedParamType)
        && !isMapType(resolvedParamType);

  }

}
