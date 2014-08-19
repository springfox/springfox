package com.mangofactory.swagger.models.alternates;

import com.fasterxml.classmate.TypeResolver;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

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

  /**
   * Helper method to create a new alternate for <code>Map&lt;K,V&gt;</code> that results in an alternate of type
   * <code>List&lt;Entry&lt;K,V&gt;&gt;</code>.
   *
   * @param key   the class that represents a key
   * @param value the value
   * @return the alternate type rule
   */
  public static AlternateTypeRule newMapRule(Class<?> key, Class<?> value) {
    TypeResolver resolver = new TypeResolver();
    return new AlternateTypeRule(resolver.resolve(Map.class, key, value),
            resolver.resolve(List.class, resolver.resolve(Entry.class, key, value)));
  }
}
