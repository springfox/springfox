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

package springfox.documentation.swagger.readers.parameter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import io.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.Enums;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import springfox.documentation.swagger.schema.ApiModelProperties;

import static com.google.common.base.Strings.*;
import static springfox.documentation.swagger.common.SwaggerPluginSupport.*;
import static springfox.documentation.swagger.readers.parameter.ParameterAnnotationReader.*;

@Component("swaggerParameterDescriptionReader")
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class ApiParamParameterBuilder implements ParameterBuilderPlugin {

  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    Optional<ApiParam> apiParam = findApiParam(methodParameter);
    context.parameterBuilder()
        .allowableValues(allowableValues(
            methodParameter,
            apiParam.transform(toAllowableValue()).or("")));
    if (apiParam.isPresent()) {
      context.parameterBuilder().name(emptyToNull(apiParam.get().name()));
      context.parameterBuilder().description(emptyToNull(apiParam.get().value()));
      context.parameterBuilder().parameterAccess(emptyToNull(apiParam.get().access()));
      context.parameterBuilder().defaultValue(emptyToNull(apiParam.get().defaultValue()));
      context.parameterBuilder().allowMultiple(apiParam.get().allowMultiple());
      context.parameterBuilder().required(apiParam.get().required());
    }
  }

  @VisibleForTesting
  Optional<ApiParam> findApiParam(MethodParameter methodParameter) {
    return apiParam(methodParameter);
  }

  private Function<ApiParam, String> toAllowableValue() {
    return new Function<ApiParam, String>() {
      @Override
      public String apply(ApiParam input) {
        return input.allowableValues();
      }
    };
  }

  private AllowableValues allowableValues(MethodParameter methodParameter, String allowableValueString) {
    AllowableValues allowableValues = null;
    if (!isNullOrEmpty(allowableValueString)) {
      allowableValues = ApiModelProperties.allowableValueFromString(allowableValueString);
    } else {
      if (methodParameter.getParameterType().isEnum()) {
        allowableValues = Enums.allowableValues(methodParameter.getParameterType());
      }
      if (methodParameter.getParameterType().isArray()) {
        allowableValues = Enums.allowableValues(methodParameter.getParameterType().getComponentType());
      }
    }
    return allowableValues;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }
}
