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
package springfox.documentation.swagger.readers.parameter;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ExampleBuilder;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.ParameterStyle;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spring.web.DescriptionResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static springfox.documentation.swagger.common.SwaggerPluginSupport.*;
import static springfox.documentation.swagger.readers.parameter.Examples.*;

@Order(OAS_PLUGIN_ORDER)
@Component
@SuppressWarnings("deprecation")
public class OpenApiParameterBuilder implements ParameterBuilderPlugin {
  private final DescriptionResolver descriptions;

  @Autowired
  public OpenApiParameterBuilder(DescriptionResolver descriptions) {
    this.descriptions = descriptions;
  }

  @Override
  public void apply(ParameterContext context) {
    Optional<Parameter> parameterAnnotation = context.resolvedMethodParameter().findAnnotation(Parameter.class);
    AllowableValues allowedValues =
        new AllowableListValues(parameterAnnotation
            .map(p -> Arrays.asList(p.schema().allowableValues()))
            .orElse(new ArrayList<>()), "LIST");
    //TODO: this is not entirely accurate, a collection type should look at p.array()

    context.parameterBuilder().allowableValues(allowedValues);

    context.requestParameterBuilder()
        .query(q -> q.enumerationFacet(e -> e.allowedValues(allowedValues)));
    //TODO: Handle other facets

    if (parameterAnnotation.isPresent()) {
      Parameter annotation = parameterAnnotation.get();
      Example example = null;
      if (annotation.example().length() > 0) {
        example = new ExampleBuilder()
            .value(annotation.example())
            .build();
      }
      Optional<ScalarType> scalarType = ScalarType.from(annotation.schema().type(), annotation.schema().format());
      context.requestParameterBuilder()
          .name(annotation.name())
          .description(descriptions.resolve(annotation.description()))
          .required(annotation.required())
          .hidden(annotation.hidden())
          .precedence(OAS_PLUGIN_ORDER)
          .query(q ->
              q.model(m -> scalarType.ifPresent(m::maybeConvertToScalar))
                  .defaultValue(annotation.schema().defaultValue())
                  .allowEmptyValue(annotation.allowEmptyValue())
                  .explode(translateExplodeOption(annotation.explode()))
                  .style(translateStyle(annotation.style())))
          .example(example)
          .examples(allExamples("", annotation.examples()))
          .examples(contentExamples(annotation.content()));
    }
  }

  private ParameterStyle translateStyle(io.swagger.v3.oas.annotations.enums.ParameterStyle style) {
    return ParameterStyle.valueOf(style.name());
  }

  private Boolean translateExplodeOption(Explode explode) {
    if (explode == Explode.DEFAULT) {
      return null;
    }
    return explode == Explode.TRUE;
  }

  private Collection<Example> contentExamples(Content[] contents) {
    return Arrays.stream(contents)
        .flatMap(c -> allExamples(c.mediaType(), c.examples()).stream())
        .collect(Collectors.toList());
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }
}
