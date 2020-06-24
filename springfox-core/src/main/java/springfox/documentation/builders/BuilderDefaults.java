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

package springfox.documentation.builders;

import com.fasterxml.classmate.ResolvedType;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Optional.*;
import static springfox.documentation.schema.Enums.*;

/**
 * This is a utility class with useful methods for builders
 */
public class BuilderDefaults {
  private BuilderDefaults() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns this default value if the new value is null
   *
   * @param newValue     - new value
   * @param defaultValue - default value
   * @param <T>          - Represents any type that is nullable
   * @return Coalesces the newValue and defaultValue to return a non-null value
   */
  public static <T> T defaultIfAbsent(
      T newValue,
      T defaultValue) {
    return ofNullable(newValue)
        .orElse(ofNullable(defaultValue)
                    .orElse(null));
  }

  /**
   * Returns an empty list if the newValue is null
   *
   * @param newValue - a list
   * @param <T>      - any type
   * @return non-null list
   */
  public static <T> List<T> nullToEmptyList(Collection<T> newValue) {
    if (newValue == null) {
      return new ArrayList<>();
    }
    return new ArrayList<>(newValue);
  }

  /**
   * Returns an empty list if the newValue is null
   *
   * @param newValues - an array
   * @param <T>       - any type
   * @return non-null list
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] nullToEmptyArray(T[] newValues) {
    if (newValues == null) {
      return (T[]) new Object[] {};
    }
    return newValues;
  }

  /**
   * Returns an empty map if the input is null
   *
   * @param newValue - nullable map value
   * @param <K>      - map key
   * @param <V>      - map value
   * @return non-null Map
   */
  public static <K, V> Map<K, V> nullToEmptyMap(Map<K, V> newValue) {
    if (newValue == null) {
      return new HashMap<>();
    }
    return newValue;
  }

  /**
   * Returns an empty map if the input is null
   *
   * @param newValue - nullable map value
   * @param <K>      - map key
   * @param <V>      - map value
   * @return non-null Map
   */
  public static <K, V> Map<K, List<V>> nullToEmptyMultimap(Map<K, List<V>> newValue) {
    if (newValue == null) {
      return new HashMap<>();
    }
    return newValue;
  }

  /**
   * Returns an empty set if the newValue is null
   *
   * @param newValue - a set
   * @param <T>      - any type
   * @return non-null set
   */
  public static <T> Set<T> nullToEmptySet(Set<T> newValue) {
    if (newValue == null) {
      return new HashSet<>();
    }
    return newValue;
  }

  /**
   * Coalesces the resolved type. Preservers the default value if the replacement is either null or represents
   * a type that is less specific than the default value. For e.g. if default value represents a String then
   * the replacement value has to be any value that is a subclass of Object. If it represents Object.class then
   * the default value is preferred
   *
   * @param replacement  - replacement value
   * @param defaultValue - default value
   * @return most specific resolved type
   */
  public static ResolvedType replaceIfMoreSpecific(
      ResolvedType replacement,
      ResolvedType defaultValue) {
    ResolvedType toReturn = defaultIfAbsent(replacement, defaultValue);
    if (isObject(replacement) && isNotObject(defaultValue)) {
      return defaultValue;
    }
    return toReturn;
  }

  private static boolean isNotObject(ResolvedType defaultValue) {
    return defaultValue != null &&
        !Object.class.equals(defaultValue.getErasedType());
  }

  private static boolean isObject(ResolvedType replacement) {
    return replacement != null &&
        Object.class.equals(replacement.getErasedType());
  }

  /**
   * Retains current allowable values if then new value is null
   *
   * @param newValue - new value
   * @param current  - existing values
   * @return the appropriate value
   */
  public static AllowableValues emptyToNull(
      AllowableValues newValue,
      AllowableValues current) {
    if (newValue != null) {
      if (newValue instanceof AllowableListValues) {
        return defaultIfAbsent(emptyListValuesToNull((AllowableListValues) newValue), current);
      } else {
        return defaultIfAbsent(newValue, current);
      }
    }
    return current;
  }

  public static String emptyToNull(String test) {
    if (test != null && test.length() == 0) {
      return null;
    }
    return test;
  }

  public static String nullToEmpty(String test) {
    if (test == null) {
      return "";
    }
    return test;
  }
}
