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

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import springfox.documentation.schema.Model;
import springfox.documentation.schema.ModelProvider;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import static springfox.documentation.swagger.common.SwaggerPluginSupport.*;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class ApiModelBuilder implements ModelBuilderPlugin {
  private final TypeResolver typeResolver;
  private final ModelProvider modelProvider;

  @Autowired
  public ApiModelBuilder(TypeResolver typeResolver,
                         @Qualifier("cachedModels") ModelProvider modelProvider) {
    this.typeResolver = typeResolver;
    this.modelProvider = modelProvider;
  }


  @Override
  public void apply(ModelContext context) {
    ApiModel annotation = AnnotationUtils.findAnnotation(forClass(context), ApiModel.class);
    if (annotation != null) {
      context.getBuilder().description(annotation.description());
      if(StringUtils.hasText(annotation.discriminator())){
        context.getBuilder().discriminator(annotation.discriminator());
      }

      // default parent is Void
      if (!annotation.parent().isAssignableFrom(Void.class)) {
        Optional<Model> parentModel = modelProvider.modelFor(ModelContext.fromParent(context, this.typeResolver.resolve(annotation.parent())));

        // parent model exist because @ApiModel not required on model
        // if(parentModel.isPresent()) {
          context.getBuilder().parent(parentModel.get());
        // }
      }
    }
  }

  private Class<?> forClass(ModelContext context) {
    return typeResolver.resolve(context.getType()).getErasedType();
  }


  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }
}
