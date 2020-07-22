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
import io.swagger.annotations.ApiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.ReferenceModelSpecification;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.schema.property.ModelSpecificationFactory;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.ArrayList;
import java.util.List;

import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.swagger.common.SwaggerPluginSupport.*;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
@SuppressWarnings("deprecation")
public class ApiModelBuilder implements ModelBuilderPlugin {
  private final TypeResolver typeResolver;
  private final TypeNameExtractor typeNameExtractor;
  private final EnumTypeDeterminer enumTypeDeterminer;
  private final ModelSpecificationFactory modelSpecifications;

  @Autowired
  public ApiModelBuilder(
      TypeResolver typeResolver,
      TypeNameExtractor typeNameExtractor,
      EnumTypeDeterminer enumTypeDeterminer,
      ModelSpecificationFactory modelSpecifications) {
    this.typeResolver = typeResolver;
    this.typeNameExtractor = typeNameExtractor;
    this.enumTypeDeterminer = enumTypeDeterminer;
    this.modelSpecifications = modelSpecifications;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void apply(ModelContext context) {
    ApiModel annotation = AnnotationUtils.findAnnotation(forClass(context), ApiModel.class);
    if (annotation != null) {
      List<springfox.documentation.schema.ModelReference> modelRefs = new ArrayList<>();
      List<ReferenceModelSpecification> subclassKeys = new ArrayList<>();
      for (Class<?> each : annotation.subTypes()) {
        modelRefs.add(modelRefFactory(context, enumTypeDeterminer, typeNameExtractor)
                          .apply(typeResolver.resolve(each)));
        modelSpecifications.create(
            context,
            typeResolver.resolve(each)).getReference()
            .ifPresent(subclassKeys::add);
      }
      context.getBuilder()
             .description(annotation.description())
             .discriminator(annotation.discriminator())
             .subTypes(modelRefs);
      context.getModelSpecificationBuilder()
             .facets(f -> f.description(annotation.description()))
             .compoundModel(cm -> cm.discriminator(annotation.discriminator())
                                    .subclassReferences(subclassKeys));
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
