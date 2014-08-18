package com.mangofactory.swagger.models.property;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mangofactory.swagger.models.BeanPropertyNamingStrategy;

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

  private static Predicate<BeanPropertyDefinition> withSameInternalName(
      final BeanPropertyDefinition propertyDefinition) {

    return new Predicate<BeanPropertyDefinition>() {
      @Override
      public boolean apply(BeanPropertyDefinition input) {
        return input.getInternalName().equals(propertyDefinition.getInternalName());
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

  private static String getName(PropertyNamingStrategy naming, BeanPropertyDefinition beanProperty,
                                MapperConfig<?> config) {

    AnnotationIntrospector annotationIntrospector = config.isAnnotationProcessingEnabled()
            ? config.getAnnotationIntrospector()
            : null;
    POJOPropertyBuilder prop = new POJOPropertyBuilder(beanProperty.getName(), annotationIntrospector, true);
    return naming.nameForField(config, prop.getField(), beanProperty.getName());
  }

}
