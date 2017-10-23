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

package springfox.documentation.spring.web.readers.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.Enums;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Types.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
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

  @Override
  public void apply(ParameterExpansionContext context) {
    AllowableValues allowable = allowableValues(context.getField().getRawMember());

    String name = isNullOrEmpty(context.getParentName())
                  ? context.getField().getName()
                  : String.format("%s.%s", context.getParentName(), context.getField().getName());

    String typeName = context.getDataTypeName();
    ModelReference itemModel = null;
    ResolvedType resolved = resolver.resolve(context.getField().getType());
    if (isContainerType(resolved)) {
      resolved = fieldType(context).or(resolved);
      ResolvedType elementType = collectionElementType(resolved);
      String itemTypeName = typeNameFor(elementType.getErasedType());
      AllowableValues itemAllowables = null;
      if (enumTypeDeterminer.isEnum(elementType.getErasedType())) {
        itemAllowables = Enums.allowableValues(elementType.getErasedType());
        itemTypeName = "string";
      }
      typeName = containerType(resolved);
      itemModel = new ModelRef(itemTypeName, itemAllowables);
    } else if (enumTypeDeterminer.isEnum(resolved.getErasedType())) {
      typeName = "string";
    }
    context.getParameterBuilder()
        .name(name)
        .description(null)
        .defaultValue(null)
        .required(Boolean.FALSE)
        .allowMultiple(isContainerType(resolved))
        .type(resolved)
        .modelRef(new ModelRef(typeName, itemModel))
        .allowableValues(allowable)
        .parameterType("query")
        .parameterAccess(null);
  }

  private Optional<ResolvedType> fieldType(ParameterExpansionContext context) {
    return Optional.of(context.getField().getType());
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private AllowableValues allowableValues(final Field field) {

    AllowableValues allowable = null;
    if (enumTypeDeterminer.isEnum(field.getType())) {
      allowable = new AllowableListValues(getEnumValues(field.getType()), "LIST");
    }

    return allowable;
  }

  private List<String> getEnumValues(final Class<?> subject) {
    return transform(Arrays.asList(subject.getEnumConstants()), new Function<Object, String>() {
      @Override
      public String apply(final Object input) {
        return input.toString();
      }
    });
  }
}
