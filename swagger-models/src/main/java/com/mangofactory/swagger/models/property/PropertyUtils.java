package com.mangofactory.swagger.models.property;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * @author fgaule
 * @since 17/07/2014
 */
public class PropertyUtils {

  public static Function<BeanPropertyDefinition, String> beanPropertyByInternalName() {
    return new Function<BeanPropertyDefinition, String>() {
      @Override
      public String apply(BeanPropertyDefinition input) {
        return input.getInternalName();
      }
    };
  }

  public static Optional<BeanPropertyDefinition> jacksonPropertyWithSameInternalName(BeanDescription beanDescription,
                                                                         BeanPropertyDefinition propertyDefinition) {
    return FluentIterable.from(beanDescription.findProperties()).firstMatch(withSameInternalName(propertyDefinition));
  }

  private static Predicate<BeanPropertyDefinition> withSameInternalName(final BeanPropertyDefinition
                                                                                propertyDefinition) {
    return new Predicate<BeanPropertyDefinition>() {
      @Override
      public boolean apply(BeanPropertyDefinition input) {
        return input.getInternalName().equals(propertyDefinition.getInternalName());
      }
    };
  }
}
