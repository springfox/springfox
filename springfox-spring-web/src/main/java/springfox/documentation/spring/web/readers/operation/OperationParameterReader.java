/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

import com.fasterxml.classmate.ResolvedType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import springfox.documentation.common.Compatibility;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.spring.web.readers.parameter.ExpansionContext;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.slf4j.LoggerFactory.*;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.schema.ScalarTypes.*;

@SuppressWarnings("deprecation")
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OperationParameterReader implements OperationBuilderPlugin {
  private static final Logger LOGGER = getLogger(OperationParameterReader.class);
  private final ModelAttributeParameterExpander expander;
  private final EnumTypeDeterminer enumTypeDeterminer;
  private final ParameterAggregator aggregator;

  @Autowired
  private DocumentationPluginsManager pluginsManager;

  @Autowired
  public OperationParameterReader(
      ModelAttributeParameterExpander expander,
      EnumTypeDeterminer enumTypeDeterminer,
      ParameterAggregator aggregator) {
    this.expander = expander;
    this.enumTypeDeterminer = enumTypeDeterminer;
    this.aggregator = aggregator;
  }

  @Override
  public void apply(OperationContext context) {
    context.operationBuilder().parameters(context.getGlobalOperationParameters());
    List<Compatibility<springfox.documentation.service.Parameter, RequestParameter>> compatibilities
        = readParameters(context);
    context.operationBuilder().parameters(
        compatibilities.stream()
            .map(Compatibility::getLegacy)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList()));
    context.operationBuilder().requestParameters(new HashSet<>(context.getGlobalRequestParameters()));
    Collection<RequestParameter> requestParameters = compatibilities.stream()
        .map(Compatibility::getModern)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(toSet());
    context.operationBuilder()
        .requestParameters(aggregator.aggregate(requestParameters));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private List<Compatibility<springfox.documentation.service.Parameter, RequestParameter>>
  readParameters(OperationContext context) {
    List<ResolvedMethodParameter> methodParameters = context.getParameters();
    List<Compatibility<springfox.documentation.service.Parameter, RequestParameter>> parameters = new ArrayList<>();
    LOGGER.debug("Reading parameters for method {} at path {}", context.getName(), context.requestMappingPattern());

    int index = 0;
    for (ResolvedMethodParameter methodParameter : methodParameters) {
      LOGGER.debug("Processing parameter {}", methodParameter.defaultName().orElse("<unknown>"));
      ResolvedType alternate = context.alternateFor(methodParameter.getParameterType());
      if (!shouldIgnore(methodParameter, alternate, context.getIgnorableParameterTypes())) {

        ParameterContext parameterContext = new ParameterContext(methodParameter,
            context.getDocumentationContext(),
            context.getGenericsNamingStrategy(),
            context,
            index++);

        if (shouldExpand(methodParameter, alternate)) {
          parameters.addAll(
              expander.expand(
                  new ExpansionContext("", alternate, context)));
        } else {
          parameters.add(pluginsManager.parameter(parameterContext));
        }
      }
    }
    return parameters.stream()
        .filter(hiddenParameter().negate())
        .collect(toList());
  }

  private Predicate<Compatibility<springfox.documentation.service.Parameter, RequestParameter>> hiddenParameter() {
    return c -> c.getLegacy()
        .map(springfox.documentation.service.Parameter::isHidden)
        .orElse(false);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private boolean shouldIgnore(
      final ResolvedMethodParameter parameter,
      ResolvedType resolvedParameterType,
      final Set<Class> ignorableParamTypes) {

    if (ignorableParamTypes.contains(resolvedParameterType.getErasedType())) {
      return true;
    }
    return ignorableParamTypes.stream()
        .filter(Annotation.class::isAssignableFrom)
        .anyMatch(parameter::hasParameterAnnotation);

  }

  private boolean shouldExpand(final ResolvedMethodParameter parameter, ResolvedType resolvedParamType) {
    return !parameter.hasParameterAnnotation(RequestBody.class)
        && !parameter.hasParameterAnnotation(RequestPart.class)
        && !parameter.hasParameterAnnotation(RequestParam.class)
        && !parameter.hasParameterAnnotation(PathVariable.class)
        && !builtInScalarType(resolvedParamType.getErasedType()).isPresent()
        && !enumTypeDeterminer.isEnum(resolvedParamType.getErasedType())
        && !isContainerType(resolvedParamType)
        && !isMapType(resolvedParamType);
  }

}
