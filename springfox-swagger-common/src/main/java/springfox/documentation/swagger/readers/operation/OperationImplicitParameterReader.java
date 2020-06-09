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
import springfox.documentation.builders.CollectionElementFacetBuilder;
import springfox.documentation.builders.EnumerationElementFacetBuilder;
import springfox.documentation.builders.ExampleBuilder;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.builders.SimpleParameterSpecificationBuilder;
import springfox.documentation.common.Compatibility;
import springfox.documentation.schema.CollectionSpecification;
import springfox.documentation.schema.CollectionType;
import springfox.documentation.schema.ModelKey;
import springfox.documentation.schema.ModelKeyBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.QualifiedModelName;
import springfox.documentation.schema.ReferenceModelSpecification;
import springfox.documentation.schema.ScalarTypes;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.CollectionFormat;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Optional.*;
import static org.slf4j.LoggerFactory.*;
import static springfox.documentation.schema.Types.*;
import static springfox.documentation.schema.property.PackageNames.*;
import static springfox.documentation.swagger.common.SwaggerPluginSupport.*;
import static springfox.documentation.swagger.readers.parameter.Examples.*;
import static springfox.documentation.swagger.schema.ApiModelProperties.*;

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
    List<Compatibility<Parameter, RequestParameter>> parameters = readParameters(context);
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

  static Compatibility<Parameter, RequestParameter> implicitParameter(
      DescriptionResolver descriptions,
      ApiImplicitParam param) {
    Compatibility<ModelRef, ModelSpecification> modelRef = maybeGetModelRef(param);
    ParameterType in = ParameterType.from(param.paramType());
    return new Compatibility<>(
        new ParameterBuilder()
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
            .simpleParameterBuilder()
            .model(modelRef.getModern().orElse(null))
            .defaultValue(param.defaultValue())
            .facetBuilder(EnumerationElementFacetBuilder.class)
            .allowedValues(allowableValueFromString(param.allowableValues()))
            .yield(SimpleParameterSpecificationBuilder.class)
            .facetBuilder(CollectionElementFacetBuilder.class)
            .collectionFormat(CollectionFormat.convert(param.collectionFormat()).orElse(null))
            .yield(SimpleParameterSpecificationBuilder.class)
            .yield()
            .precedence(SWAGGER_PLUGIN_ORDER)
            .example(new ExampleBuilder().value(param.example()).build())
            .examples(examples(param.examples()).entrySet().stream()
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList()))
            .build()
    );
  }

  private static Compatibility<ModelRef, ModelSpecification> maybeGetModelRef(ApiImplicitParam param) {
    String dataType = ofNullable(param.dataType())
        .filter(((Predicate<String>) String::isEmpty).negate())
        .orElse("string");
    ModelSpecification modelSpecification = modelSpecification(param);

    AllowableValues allowableValues = null;
    if (isBaseType(dataType)) {
      allowableValues = allowableValueFromString(param.allowableValues());
    }
    if (param.allowMultiple()) {
      return new Compatibility<>(
          new ModelRef("", new ModelRef(dataType, allowableValues)),
          modelSpecification);
    }
    return new Compatibility<>(
        new ModelRef(dataType, allowableValues), modelSpecification);
  }

  private static ModelSpecification modelSpecification(
      ApiImplicitParam param) {
    Class<?> clazz;
    try {
      param.dataTypeClass();
      if (param.dataTypeClass() != Void.class) {
        clazz = param.dataTypeClass();
      } else {
        clazz = Class.forName(param.dataType());
      }
    } catch (ClassNotFoundException e) {
      LOGGER.warn(
          "Unable to interpret the implicit parameter configuration with dataType: {}, dataTypeClass: {}",
          param.dataType(),
          param.dataTypeClass());
      return null;
    }
    ModelSpecification modelSpecification = ScalarTypes.builtInScalarType(clazz)
        .map(scalar -> {
          if (param.allowMultiple()) {
            return new ModelSpecificationBuilder()
                .collectionModel(
                    new CollectionSpecification(
                        new ModelSpecificationBuilder()
                            .scalarModel(scalar)
                            .build(),
                        CollectionType.LIST))
                .build();
          }
          return new ModelSpecificationBuilder()
              .scalarModel(scalar)
              .build();
        })
        .orElse(null);
    if (modelSpecification == null) {
      ModelKey dataTypeKey = new ModelKeyBuilder()
          .qualifiedModelName(new QualifiedModelName(safeGetPackageName(clazz), clazz.getSimpleName()))
          .build();
      modelSpecification = referenceModelSpecification(dataTypeKey, param.allowMultiple());
    }
    return modelSpecification;
  }

  private static ModelSpecification referenceModelSpecification(
      ModelKey dataTypeKey,
      boolean allowMultiple) {
    if (allowMultiple) {
      return new ModelSpecificationBuilder()
          .collectionModel(
              new CollectionSpecification(
                  new ModelSpecificationBuilder()
                      .referenceModel(new ReferenceModelSpecification(dataTypeKey))
                      .build(),
                  CollectionType.LIST))
          .build();
    }
    return new ModelSpecificationBuilder()
        .referenceModel(new ReferenceModelSpecification(dataTypeKey))
        .build();
  }

  private List<Compatibility<Parameter, RequestParameter>> readParameters(OperationContext context) {
    Optional<ApiImplicitParam> annotation = context.findAnnotation(ApiImplicitParam.class);
    List<Compatibility<Parameter, RequestParameter>> parameters = new ArrayList<>();
    annotation.ifPresent(
        apiImplicitParam ->
            parameters.add(
                OperationImplicitParameterReader.implicitParameter(descriptions, apiImplicitParam)));
    return parameters;
  }
}

