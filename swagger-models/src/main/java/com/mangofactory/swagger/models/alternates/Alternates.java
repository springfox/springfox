package com.mangofactory.swagger.models.alternates;

import com.fasterxml.classmate.TypeResolver;

import java.lang.reflect.Type;

import static com.mangofactory.swagger.models.ResolvedTypes.*;

public class Alternates {

  /**
   * Helper method to create a new alternate rule.
   *
   * @param original  the original
   * @param alternate the alternate
   * @return the alternate type rule
   */
  public static AlternateTypeRule newRule(Type original, Type alternate) {
    TypeResolver resolver = new TypeResolver();
    return new AlternateTypeRule(asResolved(resolver, original), asResolved(resolver, alternate));
  }

}
