/*
 *
 *  Copyright 2017-2019 the original author or authors.
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

package springfox.documentation.schema.plugins;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.common.Compatibility;
import springfox.documentation.schema.ReferenceModelSpecification;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.schema.property.ModelSpecificationFactory;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Optional.*;
import static springfox.documentation.schema.ResolvedTypes.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@SuppressWarnings("deprecation")
public class PropertyDiscriminatorBasedInheritancePlugin implements ModelBuilderPlugin {
  private final TypeResolver typeResolver;
  private final TypeNameExtractor typeNameExtractor;
  private final ModelSpecificationFactory modelSpecifications;
  private final EnumTypeDeterminer enumTypeDeterminer;

  @Autowired
  public PropertyDiscriminatorBasedInheritancePlugin(
      TypeResolver typeResolver,
      EnumTypeDeterminer enumTypeDeterminer,
      TypeNameExtractor typeNameExtractor,
      ModelSpecificationFactory modelSpecifications) {
    this.typeResolver = typeResolver;
    this.enumTypeDeterminer = enumTypeDeterminer;
    this.typeNameExtractor = typeNameExtractor;
    this.modelSpecifications = modelSpecifications;
  }

  @Override
  public void apply(ModelContext context) {

    List<Compatibility<springfox.documentation.schema.ModelReference, ReferenceModelSpecification>> modelRefs
        = subclassReferences(context);

    if (!modelRefs.isEmpty()) {
      context.getBuilder()
          .discriminator(discriminator(context))
          .subTypes(modelRefs.stream()
              .filter(c -> c.getLegacy().isPresent())
              .map(c -> c.getLegacy().get())
              .collect(Collectors.toList()));
      context.getModelSpecificationBuilder()
          .compoundModel(cm -> cm.discriminator(discriminator(context))
              .subclassReferences(modelRefs.stream()
                  .filter(c -> c.getModern().isPresent())
                  .map(c -> c.getModern().get())
                  .collect(Collectors.toList())));
    }
  }

  private List<Compatibility<springfox.documentation.schema.ModelReference, ReferenceModelSpecification>>
  subclassReferences(ModelContext context) {
    JsonSubTypes subTypes = AnnotationUtils.getAnnotation(forClass(context), JsonSubTypes.class);
    List<Compatibility<springfox.documentation.schema.ModelReference, ReferenceModelSpecification>> modelRefs
        = new ArrayList<>();
    if (subTypes != null) {
      for (JsonSubTypes.Type each : subTypes.value()) {
        ResolvedType resolvedSubType = typeResolver.resolve(each.value());
        modelRefs.add(
            new Compatibility<>(
                modelRefFactory(
                    context,
                    enumTypeDeterminer,
                    typeNameExtractor)
                    .apply(resolvedSubType),
                modelSpecifications.create(
                    context,
                    resolvedSubType).getReference()
                    .orElse(null)));
      }
    }
    return modelRefs;
  }

  private String discriminator(ModelContext context) {
    JsonTypeInfo typeInfo = AnnotationUtils.getAnnotation(forClass(context), JsonTypeInfo.class);
    if (typeInfo != null && typeInfo.use() == JsonTypeInfo.Id.NAME) {
      if (typeInfo.include() == JsonTypeInfo.As.PROPERTY) {
        return ofNullable(typeInfo.property()).filter(((Predicate<String>) String::isEmpty).negate())
            .orElse(typeInfo.use().getDefaultPropertyName());
      }
    }
    return "";
  }

  private Class<?> forClass(ModelContext context) {
    return typeResolver.resolve(context.getType()).getErasedType();
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
