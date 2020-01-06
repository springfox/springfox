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
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Optional;

import static java.util.Optional.*;

class PojoPropertyBuilderFactory {
  private static final Logger LOG = LoggerFactory.getLogger(POJOPropertyBuilder.class);

  POJOPropertyBuilder create(MapperConfig<?> config, BeanPropertyDefinition beanProperty) {
    AnnotationIntrospector annotationIntrospector
        = config.isAnnotationProcessingEnabled()
          ? config.getAnnotationIntrospector()
          : null;
    boolean forSerialization = config instanceof SerializationConfig;
    Optional<POJOPropertyBuilder> instance =
        jackson26Instance(
            beanProperty,
            annotationIntrospector,
            forSerialization);

    if (!instance.isPresent()) {
      return jackson27AndHigherInstance(
          config,
          beanProperty,
          annotationIntrospector,
          forSerialization);
    }
    return instance.get();
  }

  /**
   * Applies to constructor
   * new POJOPropertyBuilder(
   * config,
   * annotationIntrospector,
   * forSerialization,
   * new PropertyName(beanProperty.getName()))
   */
  private POJOPropertyBuilder jackson27AndHigherInstance(
      MapperConfig<?> config,
      BeanPropertyDefinition beanProperty,
      AnnotationIntrospector annotationIntrospector,
      boolean forSerialization) {
    try {
      Constructor<POJOPropertyBuilder> constructor = constructorWithParams(
          MapperConfig.class,
          AnnotationIntrospector.class,
          Boolean.TYPE,
          PropertyName.class);

      return constructor.newInstance(
          config,
          annotationIntrospector,
          forSerialization,
          new PropertyName(beanProperty.getName()));
    } catch (Exception e) {
      throw new InstantiationError("Unable to create an instance of POJOPropertyBuilder");
    }
  }

  /**
   * Applies to constructor
   * new POJOPropertyBuilder(new PropertyName(beanProperty.getName()),  annotationIntrospector,  true);
   */
  private Optional<POJOPropertyBuilder> jackson26Instance(
      BeanPropertyDefinition beanProperty,
      AnnotationIntrospector annotationIntrospector,
      boolean forSerialization) {
    try {
      Constructor<POJOPropertyBuilder> constructor = constructorWithParams(PropertyName.class,
          AnnotationIntrospector.class,
          Boolean.TYPE);

      return of(constructor.newInstance(
          new PropertyName(beanProperty.getName()),
          annotationIntrospector,
          forSerialization));
    } catch (Exception e) {
      LOG.debug("Unable to instantiate jackson 2.6 object. Using higher version of jackson.");
    }
    return empty();
  }

  private Constructor<POJOPropertyBuilder> constructorWithParams(Class<?>... clazzes)
      throws NoSuchMethodException {
    return POJOPropertyBuilder.class.getConstructor(clazzes);
  }

}
