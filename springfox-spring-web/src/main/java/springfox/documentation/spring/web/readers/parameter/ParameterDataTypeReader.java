/*
 *
 *  Copyright 2015-2016 the original author or authors.
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

package springfox.documentation.spring.web.readers.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import java.lang.annotation.Annotation;

import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.schema.Types.*;
import static springfox.documentation.spi.schema.contexts.ModelContext.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ParameterDataTypeReader implements ParameterBuilderPlugin {
  private final TypeNameExtractor nameExtractor;
  private final TypeResolver resolver;


  @Autowired
  public ParameterDataTypeReader(TypeNameExtractor nameExtractor, TypeResolver resolver) {
    this.nameExtractor = nameExtractor;
    this.resolver = resolver;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  @Override
  public void apply(ParameterContext context) {
    ResolvedMethodParameter methodParameter = context.resolvedMethodParameter();
    ResolvedType parameterType = methodParameter.getResolvedParameterType();
    parameterType = context.alternateFor(parameterType);
    Annotation[] methodAnnotations = methodParameter.getMethodParameter().getParameterAnnotations();
    ModelReference modelRef = null;
    for (Annotation annotation : methodAnnotations) {
      if (annotation instanceof PathVariable && treatAsAString(parameterType)) {
        parameterType = resolver.resolve(String.class);
        modelRef = new ModelRef("string");
      } else if (annotation instanceof RequestParam && isMapType(parameterType)) {
        modelRef = new ModelRef("", new ModelRef("string"), true);
      } else if (annotation instanceof RequestParam && treatRequestParamAsString(parameterType)) {
        parameterType = resolver.resolve(String.class);
        modelRef = new ModelRef("string");
      }
    }
    ModelContext modelContext = inputParam(parameterType,
        context.getDocumentationType(),
        context.getAlternateTypeProvider(),
        context.getGenericNamingStrategy(),
        context.getDocumentationContext().getIgnorableParameterTypes());
    context.parameterBuilder()
            .type(parameterType)
            .modelRef(Optional.fromNullable(modelRef)
                .or(modelRefFactory(modelContext, nameExtractor).apply(parameterType)));
  }

  private boolean treatRequestParamAsString(ResolvedType parameterType) {
    return treatAsAString(parameterType) && !isContainerType(parameterType)
        || (isContainerType(parameterType) && treatAsAString(collectionElementType(parameterType)));
  }

  private boolean treatAsAString(ResolvedType parameterType) {
    return !(isBaseType(typeNameFor(parameterType.getErasedType()))
        || parameterType.getErasedType().isEnum());
  }
}
