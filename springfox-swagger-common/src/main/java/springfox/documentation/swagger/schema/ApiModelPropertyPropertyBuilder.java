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

package springfox.documentation.swagger.schema;

import com.google.common.base.Optional;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import static springfox.documentation.schema.Annotations.*;
import static springfox.documentation.swagger.schema.ApiModelProperties.*;

@Component
public class ApiModelPropertyPropertyBuilder implements ModelPropertyBuilderPlugin {
  @Override
  public void apply(ModelPropertyContext context) {
    Optional<ApiModelProperty> annotation = Optional.absent();

    if (context.getAnnotatedElement().isPresent()) {
      annotation = annotation.or(findApiModePropertyAnnotation(context.getAnnotatedElement().get()));
    }
    if (context.getBeanPropertyDefinition().isPresent()) {
      annotation = annotation.or(findPropertyAnnotation(
              context.getBeanPropertyDefinition().get(), ApiModelProperty.class));
    }
    if (annotation.isPresent()) {
      context.getBuilder()
              .allowableValues(annotation.transform(toAllowableValues()).orNull())
              .required(annotation.transform(toIsRequired()).or(false))
              .description(annotation.transform(toDescription()).orNull())
              .isHidden(annotation.transform(toHidden()).or(false))
              .type(annotation.transform(toType(context.getResolver())).orNull());
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }
}
