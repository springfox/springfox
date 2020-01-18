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
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.schema.MapSpecification;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.ScalarModelSpecification;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.schema.ScalarTypes;
import springfox.documentation.schema.SimpleParameterSpecificationBuilder;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.schema.plugins.SchemaPluginsManager;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.ViewProviderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.schema.Types.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ParameterDataTypeReader implements ParameterBuilderPlugin {
  private static final Logger LOG = LoggerFactory.getLogger(ParameterDataTypeReader.class);
  private final TypeNameExtractor nameExtractor;
  private final TypeResolver resolver;
  private final EnumTypeDeterminer enumTypeDeterminer;
  private final SchemaPluginsManager pluginsManager;

  @Autowired
  public ParameterDataTypeReader(
      SchemaPluginsManager pluginsManager,
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
    ModelSpecificationBuilder model = new ModelSpecificationBuilder("TODO");
    if (methodParameter.hasParameterAnnotation(PathVariable.class) && treatAsAString(parameterType)) {
      parameterType = resolver.resolve(String.class);
      modelRef = new ModelRef("string");
      model.withScalar(new ScalarModelSpecification(ScalarType.STRING));
   } else if (methodParameter.hasParameterAnnotation(RequestParam.class) && isMapType(parameterType)) {
      modelRef = new ModelRef("", new ModelRef("string"), true);
      ModelSpecificationBuilder map = new ModelSpecificationBuilder("TODO");
      model.withMap(
          new MapSpecification(
              new ModelSpecificationBuilder("TODO")
                  .withScalar(new ScalarModelSpecification(ScalarType.STRING))
                  .build(),
              new ModelSpecificationBuilder("TODO")
                  .withScalar(new ScalarModelSpecification(ScalarType.STRING))
                  .build()));
    } else if (methodParameter.hasParameterAnnotation(RequestParam.class) && treatRequestParamAsString(parameterType)) {
      parameterType = resolver.resolve(String.class);
      modelRef = new ModelRef("string");
      model.withScalar(new ScalarModelSpecification(ScalarType.STRING));
    }
    if (!methodParameter.hasParameterAnnotations()) {
      String typeName = typeNameFor(parameterType.getErasedType());
      ScalarTypes.builtInScalarType(parameterType.getErasedType())
          .ifPresent(s -> model.withScalar(new ScalarModelSpecification(s)));
      if (isBaseType(typeName)) {
        modelRef = new ModelRef(typeName);
      } else {
        LOG.warn(
            "Trying to infer dataType {}",
            parameterType);
      }
    }
    ViewProviderPlugin viewProvider = pluginsManager
        .viewProvider(context.getDocumentationContext().getDocumentationType());

    ModelContext modelContext = context.getOperationContext()
        .operationModelsBuilder()
        .addInputParam(
            parameterType,
            viewProvider.viewFor(
                parameterType,
                methodParameter),
            new HashSet<>());

    Map<String, String> knownNames = new HashMap<>();
    Optional.ofNullable(context.getOperationContext().getKnownModels().get(modelContext.getParameterId()))
        .orElse(new HashSet<>())
        .forEach(m -> knownNames.put(
            m.getId(),
            m.getName()));

    context.parameterBuilder()
        .type(parameterType)
        .modelRef(Optional.ofNullable(modelRef)
            .orElse(modelRefFactory(
                modelContext,
                enumTypeDeterminer,
                nameExtractor).apply(parameterType)));
    context.requestParameterBuilder()
        .withParameterSpecification(
            new SimpleParameterSpecificationBuilder()
                .build());
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
