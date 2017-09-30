/*
 *
 *  Copyright 2015-2018 the original author or authors.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.ProjectionProviderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.schema.Types.*;
import static springfox.documentation.spi.schema.contexts.ModelContext.*;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ParameterDataTypeReader implements ParameterBuilderPlugin {
  private static final Logger LOG = LoggerFactory.getLogger(ParameterDataTypeReader.class);
  private final TypeNameExtractor nameExtractor;
  private final TypeResolver resolver;
  private final EnumTypeDeterminer enumTypeDeterminer;
  private final DocumentationPluginsManager pluginsManager;

  @Autowired
  public ParameterDataTypeReader(
      DocumentationPluginsManager pluginsManager,
      TypeNameExtractor nameExtractor,
      TypeResolver resolver,
      EnumTypeDeterminer enumTypeDeterminer) {
    this.nameExtractor = nameExtractor;
    this.resolver = resolver;
    this.enumTypeDeterminer = enumTypeDeterminer;
    this.pluginsManager = pluginsManager;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  @Override
  public void apply(ParameterContext context) {
    ResolvedMethodParameter methodParameter = context.resolvedMethodParameter();
    ResolvedType parameterType = methodParameter.getParameterType();
    parameterType = context.alternateFor(parameterType);
    ModelReference modelRef = null;
    if (methodParameter.hasParameterAnnotation(PathVariable.class) && treatAsAString(parameterType)) {
      parameterType = resolver.resolve(String.class);
      modelRef = new ModelRef("string");
    } else if (methodParameter.hasParameterAnnotation(RequestParam.class) && isMapType(parameterType)) {
      modelRef = new ModelRef("", new ModelRef("string"), true);
    } else if (methodParameter.hasParameterAnnotation(RequestParam.class) && treatRequestParamAsString(parameterType)) {
      parameterType = resolver.resolve(String.class);
      modelRef = new ModelRef("string");
    }
    if (!methodParameter.hasParameterAnnotations()) {
      String typeName = typeNameFor(parameterType.getErasedType());
      if (isBaseType(typeName)) {
        modelRef = new ModelRef(typeName);
      } else {
        LOG.warn("Trying to infer dataType {}", parameterType);
      }
    }
    
    ProjectionProviderPlugin projectionProvider = 
        pluginsManager.projectionProvider(context.getDocumentationContext().getDocumentationType());
    Optional<? extends Annotation> annotation = Optional.absent();
    if (projectionProvider.getRequiredAnnotation().isPresent()) {
      annotation = methodParameter.findAnnotation(projectionProvider.getRequiredAnnotation().get());
    }
    Optional<ResolvedType> projection = Optional.absent();
    List<ResolvedType> projections = projectionProvider.projectionsFor(parameterType, annotation);
    if (!projections.isEmpty()) {
      projection = Optional.of(projections.get(0));
      LOG.debug("Found projection {} for type {}", resolvedTypeSignature(projections.get(0)).or("<null>"), resolvedTypeSignature(parameterType).or("<null>"));
    }
    
    ModelContext modelContext = inputParam(
        context.getGroupName(),
        parameterType,
        projection,
        new HashSet<ResolvedType>(),
        context.getDocumentationType(),
        context.getAlternateTypeProvider(),
        context.getGenericNamingStrategy(),
        context.getIgnorableParameterTypes());
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
                 || enumTypeDeterminer.isEnum(parameterType.getErasedType()));
  }
}
