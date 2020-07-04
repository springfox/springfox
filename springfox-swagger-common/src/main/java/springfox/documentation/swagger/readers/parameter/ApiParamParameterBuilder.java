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

import com.fasterxml.classmate.ResolvedType;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ExampleBuilder;
import springfox.documentation.schema.Collections;
import springfox.documentation.schema.Enums;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.CollectionFormat;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.schema.ApiModelProperties;

import java.util.Optional;

import static org.springframework.util.StringUtils.*;
import static springfox.documentation.swagger.common.SwaggerPluginSupport.*;
import static springfox.documentation.swagger.readers.parameter.Examples.*;

@Component("swaggerParameterDescriptionReader")
@Order(SWAGGER_PLUGIN_ORDER)
@SuppressWarnings("deprecation")
public class ApiParamParameterBuilder implements ParameterBuilderPlugin {
  private final DescriptionResolver descriptions;
  private final EnumTypeDeterminer enumTypeDeterminer;

  @Autowired
  public ApiParamParameterBuilder(
      DescriptionResolver descriptions,
      EnumTypeDeterminer enumTypeDeterminer) {
    this.descriptions = descriptions;
    this.enumTypeDeterminer = enumTypeDeterminer;
  }

  @Override
  public void apply(ParameterContext context) {
    Optional<ApiParam> apiParam = context.resolvedMethodParameter().findAnnotation(ApiParam.class);
    AllowableValues allowedValues = allowableValues(
        context.alternateFor(context.resolvedMethodParameter().getParameterType()),
        apiParam.map(ApiParam::allowableValues).orElse(""));

    context.parameterBuilder()
        .allowableValues(allowedValues);

    if (allowedValues instanceof AllowableListValues) {
      context.requestParameterBuilder()
          .query(q -> q.enumerationFacet(e -> e.allowedValues(allowedValues)));
    } else if (allowedValues instanceof AllowableRangeValues) {
      context.requestParameterBuilder()
          .query(q -> q.numericFacet(n -> n.from((AllowableRangeValues) allowedValues)));
    }

    if (apiParam.isPresent()) {
      ApiParam annotation = apiParam.get();
      Example example = null;
      if (annotation.example().length() > 0) {
        example = new ExampleBuilder()
            .value(annotation.example())
            .build();
      }
      Optional<ScalarType> scalarType = ScalarType.from(annotation.type(), annotation.format());
      context.parameterBuilder()
          .name(annotation.name())
          .description(descriptions.resolve(annotation.value()))
          .parameterAccess(annotation.access())
          .defaultValue(annotation.defaultValue())
          .allowMultiple(annotation.allowMultiple())
          .allowEmptyValue(annotation.allowEmptyValue())
          .required(annotation.required())
          .scalarExample(example)
          .complexExamples(examples(annotation.examples()))
          .hidden(annotation.hidden())
          .collectionFormat(annotation.collectionFormat())
          .order(SWAGGER_PLUGIN_ORDER);
      context.requestParameterBuilder()
          .name(annotation.name())
          .description(descriptions.resolve(annotation.value()))
          .required(annotation.required())
          .hidden(annotation.hidden())
          .precedence(SWAGGER_PLUGIN_ORDER)
          .query(q ->
              q.model(m -> scalarType.ifPresent(m::maybeConvertToScalar))
                  .collectionFormat(CollectionFormat.convert(annotation.collectionFormat())
                      .orElse(null))
                  .defaultValue(annotation.defaultValue())
                  .allowEmptyValue(annotation.allowEmptyValue())
          )
          .example(example)
          .examples(allExamples(annotation.examples()));
    }
  }

  private AllowableValues allowableValues(
      ResolvedType parameterType,
      String allowableValueString) {
    AllowableValues allowableValues = null;
    if (!isEmpty(allowableValueString)) {
      allowableValues = ApiModelProperties.allowableValueFromString(allowableValueString);
    } else {
      if (enumTypeDeterminer.isEnum(parameterType.getErasedType())) {
        allowableValues = Enums.allowableValues(parameterType.getErasedType());
      }
      if (Collections.isContainerType(parameterType)) {
        allowableValues = Enums.allowableValues(Collections.collectionElementType(parameterType).getErasedType());
      }
    }
    return allowableValues;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }
}
