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

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.*;
import static springfox.documentation.schema.ResolvedTypes.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PropertyDiscriminatorBasedInheritancePlugin implements ModelBuilderPlugin {
  private final TypeResolver typeResolver;
  private final TypeNameExtractor typeNameExtractor;

  @Autowired
  public PropertyDiscriminatorBasedInheritancePlugin(
      TypeResolver typeResolver,
      TypeNameExtractor typeNameExtractor) {
    this.typeResolver = typeResolver;
    this.typeNameExtractor = typeNameExtractor;
  }

  @Override
  public void apply(ModelContext context) {

    List<ModelReference> modelRefs =  modelRefs(context);

    if (!modelRefs.isEmpty()) {
      context.getBuilder()
          .discriminator(discriminator(context))
          .subTypes(modelRefs);
    }
  }

  private List<ModelReference> modelRefs(ModelContext context) {
    JsonSubTypes subTypes = AnnotationUtils.getAnnotation(forClass(context), JsonSubTypes.class);
    List<ModelReference> modelRefs = new ArrayList<ModelReference>();
    if (subTypes != null) {
      for (JsonSubTypes.Type each : subTypes.value()) {
        modelRefs.add(modelRefFactory(context, typeNameExtractor)
            .apply(typeResolver.resolve(each.value())));
      }
    }
    return modelRefs;
  }

  private String discriminator(ModelContext context) {
    JsonTypeInfo typeInfo = AnnotationUtils.getAnnotation(forClass(context), JsonTypeInfo.class);
    if (typeInfo != null && typeInfo.use() == JsonTypeInfo.Id.NAME) {
      if (typeInfo.include() == JsonTypeInfo.As.PROPERTY) {
        return Optional.fromNullable(emptyToNull(typeInfo.property()))
            .or(typeInfo.use().getDefaultPropertyName());
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
