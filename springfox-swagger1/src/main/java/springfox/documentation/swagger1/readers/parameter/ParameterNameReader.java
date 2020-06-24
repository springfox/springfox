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

package springfox.documentation.swagger1.readers.parameter;

import io.swagger.annotations.ApiParam;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Optional.*;
import static springfox.documentation.spring.web.readers.parameter.ParameterTypeReader.*;

@Component("swagger1ParameterNameReader")
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
@SuppressWarnings("deprecation")
public class ParameterNameReader implements ParameterBuilderPlugin {

  @Override
  public void apply(ParameterContext context) {
    Optional<ApiParam> apiParam = context.resolvedMethodParameter().findAnnotation(ApiParam.class);
    String paramType = findParameterType(context);
    String name = null;
    if (apiParam.isPresent()) {
      name = ofNullable(apiParam.get().name()).filter(((Predicate<String>) String::isEmpty).negate()).orElse(null);
    }
    context.parameterBuilder().name(maybeOverrideName(name, paramType));
    context.requestParameterBuilder().name(maybeOverrideName(name, paramType));
  }

  private String maybeOverrideName(String parameterName, String paramType) {
    if ("body".equals(paramType)) {
      return paramType;
    }
    return parameterName;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return DocumentationType.SWAGGER_12.equals(delimiter);
  }
}
