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

package springfox.documentation.schema.property;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import springfox.documentation.spi.schema.contexts.ModelContext;

public class BeanPropertyDefinitions {
  private BeanPropertyDefinitions() {
    throw new UnsupportedOperationException();
  }

  public static Function<BeanPropertyDefinition, String> beanPropertyByInternalName() {
    return new Function<BeanPropertyDefinition, String>() {
      @Override
      public String apply(BeanPropertyDefinition input) {
        return input.getInternalName();
      }
    };
  }

  public static String name(BeanPropertyDefinition beanPropertyDefinition,
                            boolean forSerialization, BeanPropertyNamingStrategy namingStrategy) {

    return forSerialization
            ? namingStrategy.nameForSerialization(beanPropertyDefinition)
            : namingStrategy.nameForDeserialization(beanPropertyDefinition);
  }

  public static Optional<BeanPropertyDefinition> jacksonPropertyWithSameInternalName(BeanDescription beanDescription,
      BeanPropertyDefinition propertyDefinition) {

    return FluentIterable.from(beanDescription.findProperties())
            .firstMatch(withSameInternalName(propertyDefinition));
  }

  public static Predicate<springfox.documentation.schema.ModelProperty> ignorable(final ModelContext givenContext) {
    return new Predicate<springfox.documentation.schema.ModelProperty>() {
      @Override
      public boolean apply(springfox.documentation.schema.ModelProperty input) {
        return givenContext.hasSeenBefore(input.getType());
      }
    };
  }

  public static Function<PropertyNamingStrategy, String> overTheWireName(final BeanPropertyDefinition beanProperty,
      final MapperConfig<?> config) {

    return new Function<PropertyNamingStrategy, String>() {
      @Override
      public String apply(PropertyNamingStrategy strategy) {
        return getName(strategy, beanProperty, config);
      }
    };
  }

  private static Predicate<BeanPropertyDefinition> withSameInternalName(
      final BeanPropertyDefinition propertyDefinition) {

    return new Predicate<BeanPropertyDefinition>() {
      @Override
      public boolean apply(BeanPropertyDefinition input) {
        return input.getInternalName().equals(propertyDefinition.getInternalName());
      }
    };
  }

  private static String getName(PropertyNamingStrategy naming, BeanPropertyDefinition beanProperty,
                                MapperConfig<?> config) {

    AnnotationIntrospector annotationIntrospector = config.isAnnotationProcessingEnabled()
            ? config.getAnnotationIntrospector()
            : null;
    POJOPropertyBuilder prop
            = new POJOPropertyBuilder(new PropertyName(beanProperty.getName()),  annotationIntrospector,  true);
    return naming.nameForField(config, prop.getField(), beanProperty.getName());
  }
}
