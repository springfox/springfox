/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
package springfox.documentation.spring.data.rest;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class RequestExtractionUtils {
  private RequestExtractionUtils() {
    throw new UnsupportedOperationException();
  }

  static String lowerCamelCaseName(String stringValue) {
    return Character.toLowerCase(stringValue.charAt(0)) + stringValue.substring(1);
  }

  static String upperCamelCaseName(String stringValue) {
    return Character.toUpperCase(stringValue.charAt(0)) + stringValue.substring(1);
  }

  static String actionName(PersistentEntity<?, ?> entity, Method method) {
    return String.format("%s%s", method.getName(), entity.getType().getSimpleName());
  }

  static List<Annotation> pathAnnotations(String name) {
    return pathAnnotations(name, null);
  }

  static List<Annotation> pathAnnotations(String name, HandlerMethod handler) {
    List<Annotation> annotations = handlerAnnotations(handler);
    if (name != null) {
      annotations.add(SynthesizedAnnotations.pathVariable(name));
    }
    return annotations;
  }

  static List<Annotation> bodyAnnotations(HandlerMethod handler) {
    List<Annotation> annotations = handlerAnnotations(handler);
    annotations.add(SynthesizedAnnotations.REQUEST_BODY_ANNOTATION);
    return annotations;
  }

  private static List<Annotation> handlerAnnotations(HandlerMethod handler) {
    List<Annotation> annotations = new ArrayList<>();
    if (handler != null) {
      annotations.addAll(Arrays.asList(AnnotationUtils.getAnnotations(handler.getMethod())));
    }
    return annotations;
  }

  static List<Annotation> bodyAnnotations() {
    List<Annotation> annotations = handlerAnnotations(null);
    annotations.add(SynthesizedAnnotations.REQUEST_BODY_ANNOTATION);
    return annotations;
  }

  static String propertyIdentifierName(PersistentProperty<?> property) {
    String propertyName = property.getName();
    if (property.isCollectionLike()) {
      propertyName = property.getComponentType().getSimpleName();
    } else if (property.isMap()) {
      propertyName = property.getMapValueType().getSimpleName();
    }
    return String.format("%sId", propertyName.toLowerCase());
  }

  static ResolvedType propertyResponse(PersistentProperty<?> property, TypeResolver resolver) {
    if (property.isCollectionLike()) {
      return resolver.resolve(CollectionModel.class, property.getComponentType());
    } else if (property.isMap()) {
      return resolver.resolve(
          EntityModel.class,
          resolver.resolve(
              Map.class,
              String.class,
              property.getMapValueType()));
    }
    return resolver.resolve(EntityModel.class, property.getType());
  }

  static ResolvedType propertyItemResponse(PersistentProperty<?> property, TypeResolver resolver) {
    if (property.isCollectionLike()) {
      return resolver.resolve(EntityModel.class, property.getComponentType());
    } else if (property.isMap()) {
      return resolver.resolve(
          EntityModel.class,
          property.getMapValueType());
    }
    return resolver.resolve(EntityModel.class, property.getType());
  }
}
