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

package springfox.documentation.spring.web.readers.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.schema.Enums;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.schema.ScalarTypes;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.CollectionFormat;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Optional.*;
import static java.util.stream.Collectors.*;
import static org.springframework.util.StringUtils.*;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.service.RequestParameter.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@SuppressWarnings("deprecation")
public class ExpandedParameterBuilder implements ExpandedParameterBuilderPlugin {
  private final TypeResolver resolver;
  private final EnumTypeDeterminer enumTypeDeterminer;

  @Autowired
  public ExpandedParameterBuilder(
      TypeResolver resolver,
      EnumTypeDeterminer enumTypeDeterminer) {
    this.resolver = resolver;
    this.enumTypeDeterminer = enumTypeDeterminer;
  }

  @SuppressWarnings("InnerAssignment")
  @Override
  public void apply(ParameterExpansionContext context) {
    AllowableValues allowable = allowableValues(context.getFieldType().getErasedType());

    String name = isEmpty(context.getParentName())
        ? context.getFieldName()
        : String.format("%s.%s", context.getParentName(), context.getFieldName());

    String typeName = context.getDataTypeName();
    springfox.documentation.schema.ModelReference itemModel = null;
    ResolvedType resolved = fieldType(context).orElse(resolver.resolve(context.getFieldType()));
    ModelSpecification modelSpecification;
    if (isContainerType(resolved)) {
      ResolvedType elementType = collectionElementType(resolved);
      String itemTypeName = springfox.documentation.schema.Types.typeNameFor(elementType.getErasedType());
      AllowableValues itemAllowables = null;
      if (enumTypeDeterminer.isEnum(elementType.getErasedType())) {
        allowable = itemAllowables = Enums.allowableValues(elementType.getErasedType());
        itemTypeName = "string";
      }
      typeName = containerType(resolved);
      itemModel = new springfox.documentation.schema.ModelRef(itemTypeName, itemAllowables);
      modelSpecification = new ModelSpecificationBuilder()
          .collectionModel(c ->
              c.model(m ->
                  m.scalarModel(scalarType(elementType))
                      .facets(f -> f.enumerationFacet(
                          e -> e.allowedValues(Enums.allowableValues(elementType.getErasedType())))))
                  .collectionType(collectionType(resolved)).build())
          .facets(f -> f.enumerationFacet(
              e -> e.allowedValues(Enums.allowableValues(elementType.getErasedType()))))
          .build();
    } else if (enumTypeDeterminer.isEnum(resolved.getErasedType())) {
      typeName = "string";
      modelSpecification = new ModelSpecificationBuilder()
          .scalarModel(ScalarType.STRING)
          .facets(f -> f.enumerationFacet(
              e -> e.allowedValues(Enums.allowableValues(resolved.getErasedType()))
                  .build()))
          .build();
    } else {
      modelSpecification = new ModelSpecificationBuilder()
          .scalarModel(ScalarTypes.builtInScalarType(resolved)
              .orElse(ScalarType.STRING))
          .facets(f -> f.enumerationFacet(
              e -> e.allowedValues(Enums.allowableValues(resolved.getErasedType()))
                  .build()))
          .build();
    }
    context.getParameterBuilder()
        .name(name)
        .description(null)
        .defaultValue(null)
        .required(Boolean.FALSE)
        .allowMultiple(isContainerType(resolved))
        .type(resolved)
        .modelRef(new springfox.documentation.schema.ModelRef(typeName, itemModel))
        .allowableValues(allowable)
        .parameterType(context.getParameterType())
        .order(DEFAULT_PRECEDENCE)
        .parameterAccess(null);

    AllowableValues finalAllowable = allowable;
    context.getRequestParameterBuilder()
        .name(name)
        .description(null)
        .required(Boolean.FALSE)
        .in(context.getParameterType())
        .precedence(DEFAULT_PRECEDENCE)
        .query(q -> q.collectionFormat(isContainerType(resolved) ? CollectionFormat.MULTI : null)
            .model(m -> m.copyOf(modelSpecification))
            .enumerationFacet(e -> e.allowedValues(finalAllowable)));
  }

  private ScalarType scalarType(ResolvedType elementType) {
    ScalarType scalarModelSpecification = ScalarTypes.builtInScalarType(elementType)
        .orElse(ScalarType.STRING);
    if (enumTypeDeterminer.isEnum(elementType.getErasedType())) {
      scalarModelSpecification = ScalarType.STRING;
    }
    return scalarModelSpecification;
  }

  private Optional<ResolvedType> fieldType(ParameterExpansionContext context) {
    return of(context.getFieldType());
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private AllowableValues allowableValues(Class<?> fieldType) {

    AllowableValues allowable = null;
    if (enumTypeDeterminer.isEnum(fieldType)) {
      allowable = new AllowableListValues(getEnumValues(fieldType), "LIST");
    }

    return allowable;
  }

  private List<String> getEnumValues(final Class<?> subject) {
    return Stream.of(subject.getEnumConstants())
        .map((Function<Object, String>) Object::toString)
        .collect(toList());
  }
}
