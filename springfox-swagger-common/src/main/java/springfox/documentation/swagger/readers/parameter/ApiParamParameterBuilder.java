/*
 *
 *  Copyright 2015-2017 the original author or authors.
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

import static springfox.documentation.swagger.common.SwaggerPluginSupport.pluginDoesApply;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.classmate.ResolvedType;

import io.swagger.annotations.ApiParam;
import springfox.documentation.schema.Collections;
import springfox.documentation.schema.Enums;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import springfox.documentation.swagger.schema.ApiModelProperties;
import springfox.documentation.util.Strings;

@Component("swaggerParameterDescriptionReader")
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
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
    context.parameterBuilder()
        .allowableValues(allowableValues(
            context.alternateFor(context.resolvedMethodParameter().getParameterType()),
            apiParam.map(toAllowableValue()).orElse("")));
    if (apiParam.isPresent()) {
      ApiParam annotation = apiParam.get();
      context.parameterBuilder().name(Strings.emptyToNull(annotation.name()));
      context.parameterBuilder().description(Strings.emptyToNull(descriptions.resolve(annotation.value())));
      context.parameterBuilder().parameterAccess(Strings.emptyToNull(annotation.access()));
      context.parameterBuilder().defaultValue(Strings.emptyToNull(annotation.defaultValue()));
      context.parameterBuilder().allowMultiple(annotation.allowMultiple());
      context.parameterBuilder().required(annotation.required());
      context.parameterBuilder().hidden(annotation.hidden());
    }
  }

  private Function<ApiParam, String> toAllowableValue() {
    return new Function<ApiParam, String>() {
      @Override
      public String apply(ApiParam input) {
        return input.allowableValues();
      }
    };
  }

  private AllowableValues allowableValues(ResolvedType parameterType, String allowableValueString) {
    AllowableValues allowableValues = null;
    if (!Strings.isNullOrEmpty(allowableValueString)) {
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
