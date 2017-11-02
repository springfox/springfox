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

import static springfox.documentation.schema.AlternateTypeRules.newRule;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.Ordered;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.ResponseEntity;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;

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
    List<AlternateTypeRule> rules = new ArrayList<>();
    for (Class<?> type : findJacksonSerializeDeserializeAnnotatedClasses()) {
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
    Class<?> serializer = Optional.ofNullable(type.getAnnotation(JsonSerialize.class))
        .map(new Function<JsonSerialize, Class>() {
          @Override
          public Class apply(JsonSerialize input) {
            return input.as();
          }
        })
        .orElse(Void.class);
    Class deserializer = Optional.ofNullable(type.getAnnotation(JsonDeserialize.class))
        .map(new Function<JsonDeserialize, Class>() {
          @Override
          public Class apply(JsonDeserialize input) {
            return input.as();
          }
        })
        .orElse(Void.class);
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
    return Optional.ofNullable(toUse);
  }

  @Override
  public int getOrder() {
    return IMMUTABLES_CONVENTION_ORDER;
  }
  
  public Set<Class<?>> findJacksonSerializeDeserializeAnnotatedClasses() {
    Set<Class<?>> annotatedClasses = new HashSet<>();
      ClassPathScanningCandidateComponentProvider provider = createComponentScanner();
      for (BeanDefinition beanDef : provider.findCandidateComponents(packagePrefix)) {
        annotatedClasses.add(asClass(beanDef.getBeanClassName()));
      }
      return annotatedClasses;
  }

  private ClassPathScanningCandidateComponentProvider createComponentScanner() {
      ClassPathScanningCandidateComponentProvider provider
              = new ClassPathScanningCandidateComponentProvider(false);
      provider.addIncludeFilter(new AnnotationTypeFilter(JsonSerialize.class));
      provider.addIncludeFilter(new AnnotationTypeFilter(JsonDeserialize.class));
      return provider;
  }

  private Class<?> asClass(String className) {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException ex) {
      LOGGER.error("Failed to load class", ex);
    }
    return null;
  }
}
