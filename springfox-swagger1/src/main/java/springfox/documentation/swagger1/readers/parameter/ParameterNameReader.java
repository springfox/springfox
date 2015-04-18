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

package springfox.documentation.swagger1.readers.parameter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import springfox.documentation.swagger.readers.parameter.ParameterAnnotationReader;

import static com.google.common.base.Optional.*;
import static com.google.common.base.Strings.*;
import static springfox.documentation.spring.web.readers.parameter.ParameterTypeReader.*;

@Component("swagger1ParameterNameReader")
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class ParameterNameReader implements ParameterBuilderPlugin {

  private ParameterAnnotationReader annotations = new ParameterAnnotationReader();

  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    Optional<ApiParam> apiParam = apiParam(methodParameter);
    String paramType = findParameterType(context);
    String name = null;
    if (apiParam.isPresent()) {
      name = emptyToNull(apiParam.get().name());
    }
    context.parameterBuilder().name(maybeOverrideName(name, paramType));
  }

  @VisibleForTesting
  Optional<ApiParam> apiParam(MethodParameter methodParameter) {
    return fromNullable(methodParameter.getParameterAnnotation(ApiParam.class))
        .or(annotations.fromHierarchy(methodParameter, ApiParam.class));
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
