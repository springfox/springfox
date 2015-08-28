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
import com.google.common.collect.FluentIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static springfox.documentation.schema.Types.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExpandedParameterBuilder implements ExpandedParameterBuilderPlugin {
  private static final Logger LOG = LoggerFactory.getLogger(ExpandedParameterBuilder.class);
  private final TypeResolver resolver;

  @Autowired
  public ExpandedParameterBuilder(TypeResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public void apply(ParameterExpansionContext context) {
    AllowableValues allowable = allowableValues(context.getField());

    String name = isNullOrEmpty(context.getParentName())
                  ? context.getField().getName()
                  : String.format("%s.%s", context.getParentName(), context.getField().getName());

    boolean isCollection = isCollection(context.getField().getType());
    String itemType = null;
    String typeName = context.getDataTypeName();
    Optional<Type> itemClazz;
    ResolvedType resolved = resolver.resolve(context.getField().getType());
    if (isCollection) {
      ParameterizedType parameterized;
      try {
        if (context.getField().getType().isArray()) {
          resolved = resolver.arrayType(context.getField().getType().getComponentType());
          itemType = simpleTypeName(context.getField().getType().getComponentType());
          typeName = "Array";
        } else {
          parameterized = (ParameterizedType) context.getField().getGenericType();
          itemClazz = FluentIterable.from(newArrayList(parameterized.getActualTypeArguments())).first();
          itemType = itemClazz.transform(simpleTypeName()).orNull();
          if (itemClazz.isPresent()) {
            resolved = resolver.resolve(context.getField().getType(), itemClazz.get());
          }
        }
      } catch (Exception e) {
        LOG.error("Unable to extract parameterized model attribute", e);
        itemType = "string";
      }

    }
    context.getParameterBuilder()
        .name(name)
        .description(null).defaultValue(null)
        .required(Boolean.FALSE)
        .allowMultiple(isCollection)
        .type(resolved)
        .modelRef(new ModelRef(typeName, itemType))
        .allowableValues(allowable)
        .parameterType("query")
        .parameterAccess(null);
  }

  private Function<? super Type, String> simpleTypeName() {
    return new Function<Type, String>() {
      @Override
      public String apply(Type input) {
        return simpleTypeName((Class<?>) input);
      }
    };
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private AllowableValues allowableValues(final Field field) {

    AllowableValues allowable = null;
    if (field.getType().isEnum()) {
      allowable = new AllowableListValues(getEnumValues(field.getType()), "LIST");
    }

    return allowable;
  }

  private boolean isCollection(Class<?> fieldType) {
    return Collection.class.isAssignableFrom(fieldType) || fieldType.isArray();
  }

  private String simpleTypeName(Class<?> erasedType) {
    if (erasedType.isEnum()) {
      return "string";
    }
    return Optional.fromNullable(typeNameFor(erasedType)).or("");
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
