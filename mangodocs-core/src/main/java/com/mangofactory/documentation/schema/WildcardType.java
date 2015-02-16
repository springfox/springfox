package com.mangofactory.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;

public class WildcardType {
  private WildcardType() {
    throw new UnsupportedOperationException();
  }

  public static boolean hasWildcards(ResolvedType type) {
    return any(type.getTypeBindings().getTypeParameters(), thatAreWildcards());
  }

  public static boolean exactMatch(ResolvedType first, ResolvedType second) {
    return first.equals(second);
  }

  public static boolean wildcardMatch(ResolvedType toMatch, ResolvedType wildcardType) {
    if (!toMatch.getErasedType().equals(wildcardType.getErasedType())) {
      return false;
    }
    TypeBindings wildcardTypeBindings = wildcardType.getTypeBindings();
    TypeBindings bindingsToMatch = toMatch.getTypeBindings();
    for (int index = 0; index < bindingsToMatch.size(); index++) {
      ResolvedType wildcardTypeBindingsBoundType = wildcardTypeBindings.getBoundType(index);
      ResolvedType bindingsToMatchBoundType = bindingsToMatch.getBoundType(index);

      if (!wildcardTypeBindingsBoundType.getErasedType().equals(WildcardType.class)
              && !wildcardMatch(bindingsToMatchBoundType, wildcardTypeBindingsBoundType)) {
        return false;
      }
    }
    return true;
  }

  static ResolvedType replaceWildcardsFrom(Iterable<ResolvedType> replaceables, ResolvedType wildcardType) {
    Iterator<ResolvedType> replaceableIterator = replaceables.iterator();
    return breadthFirstReplace(replaceableIterator, wildcardType);
  }

  static List<ResolvedType> collectReplaceables(ResolvedType replacingType, ResolvedType wildcardType) {
    return breadthFirstSearch(replacingType, wildcardType);
  }

  private static Predicate<ResolvedType> thatAreWildcards() {
    return new Predicate<ResolvedType>() {
      @Override
      public boolean apply(ResolvedType input) {
        return isWildcardType(input) || hasWildcards(input);
      }
    };
  }

  private static boolean isWildcardType(ResolvedType input) {
    return WildcardType.class.equals(input.getErasedType());
  }

  private static boolean typeBindingsAreOfSameSize(ResolvedType toMatch, ResolvedType wildcardType) {
    TypeBindings wildcardTypeBindings = wildcardType.getTypeBindings();
    TypeBindings bindingsToMatch = toMatch.getTypeBindings();
    return bindingsToMatch.size() == wildcardTypeBindings.size();
  }

  private static ResolvedType breadthFirstReplace(Iterator<ResolvedType> replaceableIterator,
                                                  ResolvedType wildcardType) {
    if (isWildcardType(wildcardType)) {
      if (replaceableIterator.hasNext()) {
        return replaceableIterator.next();
      } else {
        throw new IllegalStateException("Expecting the same number of wildcard types as the replaceables");
      }
    }
    TypeBindings wildcardTypeBindings = wildcardType.getTypeBindings();
    List<Type> bindings = newArrayList();
    for (int index = 0; index < wildcardTypeBindings.size(); index++) {
      if (isWildcardType(wildcardTypeBindings.getBoundType(index))) {
        if (replaceableIterator.hasNext()) {
          bindings.add(replaceableIterator.next());
        } else {
          throw new IllegalStateException("Count of wildcards to candidates do not match");
        }
      } else {
        bindings.add(breadthFirstReplace(replaceableIterator, wildcardTypeBindings.getBoundType(index)));
      }
    }
    return new TypeResolver().resolve(wildcardType.getErasedType(), toArray(bindings, Type.class));
  }

  private static List<ResolvedType> breadthFirstSearch(ResolvedType replacingType, ResolvedType wildcardType) {
    TypeBindings wildcardTypeBindings = wildcardType.getTypeBindings();
    TypeBindings bindingsToMatch = replacingType.getTypeBindings();
    //TODO - this fails when a controller method return type is a non paramaterized ResponseEntity
    Preconditions.checkArgument(typeBindingsAreOfSameSize(wildcardType, replacingType));
    List<ResolvedType> bindings = newArrayList();
    for (int index = 0; index < bindingsToMatch.size(); index++) {
      if (isWildcardType(wildcardTypeBindings.getBoundType(index))) {
        bindings.add(bindingsToMatch.getBoundType(index));
      } else {
        bindings.addAll(breadthFirstSearch(bindingsToMatch.getBoundType(index),
                wildcardTypeBindings.getBoundType(index)));
      }
    }
    return bindings;
  }
}
