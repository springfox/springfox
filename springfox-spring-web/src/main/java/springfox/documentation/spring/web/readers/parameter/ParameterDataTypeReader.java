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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.schema.ScalarTypes;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.schema.plugins.SchemaPluginsManager;
import springfox.documentation.schema.property.ModelSpecificationFactory;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.ViewProviderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import java.util.HashSet;
import java.util.Optional;

import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.schema.ResolvedTypes.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@SuppressWarnings("deprecation")
public class ParameterDataTypeReader implements ParameterBuilderPlugin {
  private static final Logger LOG = LoggerFactory.getLogger(ParameterDataTypeReader.class);
  private final TypeNameExtractor nameExtractor;
  private final TypeResolver resolver;
  private final EnumTypeDeterminer enumTypeDeterminer;
  private final SchemaPluginsManager pluginsManager;
  private final ModelSpecificationFactory models;

  @Autowired
  public ParameterDataTypeReader(
      SchemaPluginsManager pluginsManager,
      TypeNameExtractor nameExtractor,
      TypeResolver resolver,
      EnumTypeDeterminer enumTypeDeterminer,
      ModelSpecificationFactory models) {
    this.nameExtractor = nameExtractor;
    this.resolver = resolver;
    this.enumTypeDeterminer = enumTypeDeterminer;
    this.pluginsManager = pluginsManager;
    this.models = models;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  @SuppressWarnings({
      "CyclomaticComplexity",
      "NPathComplexity"})
  @Override
  public void apply(ParameterContext context) {
    ResolvedMethodParameter methodParameter = context.resolvedMethodParameter();
    ResolvedType parameterType = methodParameter.getParameterType();
    parameterType = context.alternateFor(parameterType);
    springfox.documentation.schema.ModelReference modelRef = null;
    ModelSpecificationBuilder model = new ModelSpecificationBuilder();
    boolean isRequestBody = false;
    if (methodParameter.hasParameterAnnotation(PathVariable.class) && treatAsAString(parameterType)) {
      parameterType = resolver.resolve(String.class);
      modelRef = new springfox.documentation.schema.ModelRef("string");
      model.scalarModel(ScalarType.STRING);

    } else if (methodParameter.hasParameterAnnotation(RequestParam.class) && isMapType(parameterType)) {
      modelRef = new springfox.documentation.schema.ModelRef(
          "",
          new springfox.documentation.schema.ModelRef("string"),
          true);
      model.mapModel(m ->
          m.key(v -> v.scalarModel(ScalarType.STRING))
              .value(v -> v.scalarModel(ScalarType.STRING)));
    } else if (methodParameter.hasParameterAnnotation(RequestParam.class) && treatRequestParamAsString(parameterType)) {
      parameterType = resolver.resolve(String.class);
      modelRef = new springfox.documentation.schema.ModelRef("string");
      model.scalarModel(ScalarType.STRING);
    }
    if (!methodParameter.hasParameterAnnotations()) {
      String typeName = springfox.documentation.schema.Types.typeNameFor(parameterType.getErasedType());
      ScalarTypes.builtInScalarType(parameterType.getErasedType())
          .ifPresent(model::scalarModel);
      if (springfox.documentation.schema.Types.isBaseType(typeName)) { //TODO: handle enums
        modelRef = new springfox.documentation.schema.ModelRef(typeName);
      } else {
        LOG.warn(
            "Unexpected data type dataType {}. Expecting a base type",
            parameterType);
      }
    }
    if (methodParameter.hasParameterAnnotation(RequestPart.class) ||
        methodParameter.hasParameterAnnotation(RequestBody.class)) {
      isRequestBody = true;
    }
    ViewProviderPlugin viewProvider = pluginsManager
        .viewProvider(context.getDocumentationContext()
            .getDocumentationType());

    ModelContext modelContext = context.getOperationContext()
        .operationModelsBuilder()
        .addInputParam(
            parameterType,
            viewProvider.viewFor(methodParameter),
            new HashSet<>());

    context.parameterBuilder()
        .type(parameterType)
        .modelRef(Optional.ofNullable(modelRef)
            .orElse(modelRefFactory(
                modelContext,
                enumTypeDeterminer,
                nameExtractor).apply(parameterType)));

    ModelSpecification parameterModel = models.create(modelContext, parameterType);
    if (isRequestBody) {
      context.requestParameterBuilder()
          .contentModel(parameterModel);
    } else {
      context.requestParameterBuilder()
          .query(q -> q.model(m -> m.copyOf(parameterModel)));
    }
  }

  private boolean treatRequestParamAsString(ResolvedType parameterType) {
    return treatAsAString(parameterType) && !isContainerType(parameterType)
        || (isContainerType(parameterType) && treatAsAString(collectionElementType(parameterType)));
  }

  private boolean treatAsAString(ResolvedType parameterType) {
    return !(springfox.documentation.schema.Types.isBaseType(
        springfox.documentation.schema.Types.typeNameFor(parameterType.getErasedType()))
        || enumTypeDeterminer.isEnum(parameterType.getErasedType()));
  }
}
