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

package springfox.documentation.swagger.readers.operation;


import io.swagger.annotations.ApiImplicitParam;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import springfox.documentation.builders.ExampleBuilder;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.common.Compatibility;
import springfox.documentation.schema.CollectionType;
import springfox.documentation.schema.ModelKey;
import springfox.documentation.schema.ModelKeyBuilder;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.schema.ScalarTypes;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.CollectionFormat;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.*;
import static org.slf4j.LoggerFactory.*;
import static springfox.documentation.schema.property.PackageNames.*;
import static springfox.documentation.swagger.common.SwaggerPluginSupport.*;
import static springfox.documentation.swagger.readers.parameter.Examples.*;
import static springfox.documentation.swagger.schema.ApiModelProperties.*;

@SuppressWarnings("deprecation")
@Component
@Order(SWAGGER_PLUGIN_ORDER)
public class OperationImplicitParameterReader implements OperationBuilderPlugin {
  private static final Logger LOGGER = getLogger(OperationImplicitParameterReader.class);
  private final DescriptionResolver descriptions;

  @Autowired
  public OperationImplicitParameterReader(
      DescriptionResolver descriptions) {
    this.descriptions = descriptions;
  }

  @Override
  public void apply(OperationContext context) {
    List<Compatibility<springfox.documentation.service.Parameter, RequestParameter>> parameters
        = readParameters(context);
    context.operationBuilder().parameters(parameters.stream()
        .map(Compatibility::getLegacy)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList()));
    context.operationBuilder().requestParameters(parameters.stream()
        .map(Compatibility::getModern)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList()));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }

  static Compatibility<springfox.documentation.service.Parameter, RequestParameter> implicitParameter(
      DescriptionResolver descriptions,
      ApiImplicitParam param) {
    Compatibility<springfox.documentation.schema.ModelRef, ModelSpecification> modelRef = maybeGetModelRef(param);
    ParameterType in = ParameterType.from(param.paramType());
    return new Compatibility<>(
        new springfox.documentation.builders.ParameterBuilder()
            .name(param.name())
            .description(descriptions.resolve(param.value()))
            .defaultValue(param.defaultValue())
            .required(param.required())
            .allowMultiple(param.allowMultiple())
            .modelRef(modelRef.getLegacy().orElse(null))
            .allowableValues(allowableValueFromString(param.allowableValues()))
            .parameterType(ofNullable(param.paramType())
                .filter(((Predicate<String>) String::isEmpty).negate())
                .orElse(null))
            .parameterAccess(param.access())
            .order(SWAGGER_PLUGIN_ORDER)
            .scalarExample(param.example())
            .complexExamples(examples(param.examples()))
            .collectionFormat(param.collectionFormat())
            .build(),
        new RequestParameterBuilder()
            .name(param.name())
            .description(descriptions.resolve(param.value()))
            .required(param.required())
            .in(in)
//            .allowMultiple(param.allowMultiple())
            .query(q -> q.model(m -> modelRef.getModern().ifPresent(m::copyOf))
                .defaultValue(param.defaultValue())
                .enumerationFacet(e -> e.allowedValues(allowableValueFromString(param.allowableValues())))
                .numericFacet(n -> n.from(allowableValueFromString(param.allowableValues())))
                .collectionFacet(c -> c.collectionFormat(
                    CollectionFormat.convert(param.collectionFormat())
                        .orElse(null))))
            .precedence(SWAGGER_PLUGIN_ORDER)
            .example(new ExampleBuilder().value(param.example()).build())
            .examples(examples(param.examples()).entrySet().stream()
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList()))
            .build()
    );
  }

  private static Compatibility<springfox.documentation.schema.ModelRef, ModelSpecification> maybeGetModelRef(
      ApiImplicitParam param) {
    String dataType = ofNullable(param.dataType())
        .filter(((Predicate<String>) String::isEmpty).negate())
        .orElse("string");
    ModelSpecification modelSpecification = modelSpecification(param);

    AllowableValues allowableValues = null;
    if (springfox.documentation.schema.Types.isBaseType(dataType)) {
      allowableValues = allowableValueFromString(param.allowableValues());
    }
    if (param.allowMultiple()) {
      return new Compatibility<>(
          new springfox.documentation.schema.ModelRef("",
              new springfox.documentation.schema.ModelRef(dataType, allowableValues)),
          modelSpecification);
    }
    return new Compatibility<>(
        new springfox.documentation.schema.ModelRef(dataType, allowableValues), modelSpecification);
  }

  static ModelSpecification modelSpecification(ApiImplicitParam param) {
    ModelSpecification scalarModel = null;
    ModelSpecification referenceModel = null;
    try {
      Class<?> clazz;
      if (param.dataTypeClass() != Void.class) {
        clazz = param.dataTypeClass();
      } else {
        clazz = ClassUtils.forName(param.dataType(), null);
      }
      scalarModel = ScalarTypes.builtInScalarType(clazz)
          .map(scalarModel(param))
          .orElse(null);

      ModelKey dataTypeKey = new ModelKeyBuilder()
          .qualifiedModelName(q ->
              q.namespace(safeGetPackageName(clazz))
                  .name(clazz.getSimpleName()))
          .build();
      referenceModel = referenceModelSpecification(dataTypeKey, param.allowMultiple());

    } catch (ClassNotFoundException e) {
      LOGGER.warn(
          "Unable to interpret the implicit parameter configuration with dataType: {}, dataTypeClass: {}",
          param.dataType(),
          param.dataTypeClass());
    }
    ModelSpecification scalarFromType = ScalarType.from(param.type(), param.format())
        .map(scalarModel(param))
        .orElse(null);

    return Stream.of(scalarModel, referenceModel, scalarFromType)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  private static Function<ScalarType, ModelSpecification> scalarModel(ApiImplicitParam param) {
    return scalar -> {
      if (scalar == null) {
        return null;
      }
      if (param.allowMultiple()) {
        return new ModelSpecificationBuilder()
            .collectionModel(c ->
                c.model(m ->
                    m.scalarModel(scalar))
                    .collectionType(CollectionType.LIST))
            .build();
      }
      return new ModelSpecificationBuilder()
          .scalarModel(scalar)
          .build();
    };
  }

  private static ModelSpecification referenceModelSpecification(
      ModelKey dataTypeKey,
      boolean allowMultiple) {
    if (allowMultiple) {
      return new ModelSpecificationBuilder()
          .collectionModel(c ->
              c.model(m ->
                  m.referenceModel(r ->
                      r.key(k ->
                          k.copyOf(dataTypeKey))))
                  .collectionType(CollectionType.LIST))
          .build();
    }
    return new ModelSpecificationBuilder()
        .referenceModel(r -> r.key(k -> k.copyOf(dataTypeKey)))
        .build();
  }

  private List<Compatibility<springfox.documentation.service.Parameter, RequestParameter>> readParameters(
      OperationContext context) {
    Optional<ApiImplicitParam> annotation = context.findAnnotation(ApiImplicitParam.class);
    List<Compatibility<springfox.documentation.service.Parameter, RequestParameter>> parameters = new ArrayList<>();
    annotation.ifPresent(
        apiImplicitParam ->
            parameters.add(
                OperationImplicitParameterReader.implicitParameter(descriptions, apiImplicitParam)));
    return parameters;
  }
}

