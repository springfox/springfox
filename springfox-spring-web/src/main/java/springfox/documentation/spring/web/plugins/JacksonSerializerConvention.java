/*
 *
 *  Copyright 2017-2018 the original author or authors.
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

package springfox.documentation.spring.web.plugins;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseEntity;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static springfox.documentation.schema.AlternateTypeRules.*;

/**
 * Class to automatically detect type substitutions given the jackson serialize/deserialize annotations
 */
public class JacksonSerializerConvention implements AlternateTypeRuleConvention {
  private static final Logger LOGGER = LoggerFactory.getLogger(JacksonSerializerConvention.class);
  private static final int IMMUTABLES_CONVENTION_ORDER = Ordered.HIGHEST_PRECEDENCE + 4000;

  private final TypeResolver resolver;
  private final String packagePrefix;

  public JacksonSerializerConvention(TypeResolver resolver, String packagePrefix) {
    this.resolver = resolver;
    this.packagePrefix = packagePrefix;
  }

  @Override
  public List<AlternateTypeRule> rules() {
    List<AlternateTypeRule> rules = newArrayList();
    Reflections reflections = new Reflections(packagePrefix);
    Set<Class<?>> serialized = reflections.getTypesAnnotatedWith(JsonSerialize.class);
    Set<Class<?>> deserialized = reflections.getTypesAnnotatedWith(JsonDeserialize.class);
    for (Class<?> type : Sets.union(serialized, deserialized)) {
      Optional<Type> found = findAlternate(type);
      if (found.isPresent()) {
        rules.add(newRule(
            resolver.resolve(type),
            resolver.resolve(found.get()), getOrder()));
        rules.add(newRule(
            resolver.resolve(ResponseEntity.class, type),
            resolver.resolve(found.get()), getOrder()));
      }
    }
    return rules;
  }

  private Optional<Type> findAlternate(Class<?> type) {
    Class serializer = Optional.fromNullable(type.getAnnotation(JsonSerialize.class))
        .transform(new Function<JsonSerialize, Class>() {
          @Override
          public Class apply(JsonSerialize input) {
            return input.as();
          }
        })
        .or(Void.class);
    Class deserializer = Optional.fromNullable(type.getAnnotation(JsonDeserialize.class))
        .transform(new Function<JsonDeserialize, Class>() {
          @Override
          public Class apply(JsonDeserialize input) {
            return input.as();
          }
        })
        .or(Void.class);
    Type toUse;
    if (serializer != deserializer) {
      LOGGER.warn("The serializer {} and deserializer {} . Picking the serializer by default",
          serializer.getName(),
          deserializer.getName());
    }
    if (serializer == Void.class && deserializer == Void.class) {
      toUse = null;
    } else if (serializer != Void.class) {
      toUse = serializer;
    } else {
      toUse = deserializer;
    }
    return Optional.fromNullable(toUse);
  }

  @Override
  public int getOrder() {
    return IMMUTABLES_CONVENTION_ORDER;
  }
}
