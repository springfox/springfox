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

package springfox.documentation.swagger.schema;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Optional;

import static java.util.Optional.*;
import static springfox.documentation.schema.Annotations.*;
import static springfox.documentation.swagger.schema.ApiModelProperties.*;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class ApiModelPropertyPropertyBuilder implements ModelPropertyBuilderPlugin {
  private final DescriptionResolver descriptions;

  @Autowired
  public ApiModelPropertyPropertyBuilder(DescriptionResolver descriptions) {
    this.descriptions = descriptions;
  }
  
  @Override
  public void apply(ModelPropertyContext context) {
    Optional<ApiModelProperty> annotation = empty();

    if (context.getAnnotatedElement().isPresent()) {
      annotation = annotation.map(Optional::of).orElse(findApiModePropertyAnnotation(context.getAnnotatedElement().get()));
    }
    if (context.getBeanPropertyDefinition().isPresent()) {
      annotation = annotation.map(Optional::of).orElse(findPropertyAnnotation(
          context.getBeanPropertyDefinition().get(),
          ApiModelProperty.class));
    }
    if (annotation.isPresent()) {
      context.getBuilder()
          .allowableValues(annotation.map(toAllowableValues()).orElse(null))
          .required(annotation.map(ApiModelProperty::required).orElse(false))
          .readOnly(annotation.map(ApiModelProperty::readOnly).orElse(false))
          .description(annotation.map(toDescription(descriptions)).orElse(null))
          .isHidden(annotation.map(ApiModelProperty::hidden).orElse(false))
          .type(annotation.map(toType(context.getResolver())).orElse(null))
          .position(annotation.map(ApiModelProperty::position).orElse(0))
          .example(annotation.map(toExample()).orElse(null));
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }
}
