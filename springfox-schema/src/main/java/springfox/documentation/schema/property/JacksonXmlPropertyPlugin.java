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
package springfox.documentation.schema.property;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.JacksonXmlPresentInClassPathCondition;
import springfox.documentation.schema.Xml;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static springfox.documentation.schema.Annotations.findPropertyAnnotation;

@Component
@Conditional(JacksonXmlPresentInClassPathCondition.class)
public class JacksonXmlPropertyPlugin implements ModelPropertyBuilderPlugin {

  @Override
  public void apply(ModelPropertyContext context) {
    Optional<JacksonXmlProperty> propertyAnnotation = findAnnotation(context, JacksonXmlProperty.class);

    if (propertyAnnotation.isPresent()) {
      if (propertyAnnotation.get().isAttribute()) {
        context.getBuilder()
            .xml(new Xml()
                .attribute(true)
                .namespace(defaultToNull(propertyAnnotation.get().namespace()))
                .name(propertyName(propertyAnnotation))
                .wrapped(false));
      } else {
        Optional<JacksonXmlElementWrapper> wrapper = findAnnotation(context, JacksonXmlElementWrapper.class);
        context.getBuilder()
            .xml(new Xml()
                .attribute(false)
                .namespace(defaultToNull(propertyAnnotation.get().namespace()))
                .name(wrapperName(wrapper, propertyAnnotation))
                .wrapped(wrapper.isPresent()));
      }
    }
  }

  private static <T extends Annotation> Optional<T> findAnnotation(
      ModelPropertyContext context,
      Class<T> annotationClass) {
    Optional<T> annotation = empty();
    if (context.getAnnotatedElement().isPresent()) {
      annotation = annotation.map(Optional::of).orElse(findAnnotation(
          context.getAnnotatedElement().get(),
          annotationClass));
    }
    if (context.getBeanPropertyDefinition().isPresent()) {
      annotation = annotation.map(Optional::of).orElse(findPropertyAnnotation(
          context.getBeanPropertyDefinition().get(),
          annotationClass));
    }
    return annotation;
  }

  public static <T extends Annotation> Optional<T> findAnnotation(
      AnnotatedElement annotated,
      Class<T> annotation) {
    return ofNullable(AnnotationUtils.getAnnotation(annotated, annotation));
  }

  private String wrapperName(Optional<JacksonXmlElementWrapper> wrapper, Optional<JacksonXmlProperty> property) {
    if (wrapper.isPresent() && wrapper.get().useWrapping()) {
      return ofNullable(defaultToNull(ofNullable(wrapper.get().localName())
          .filter(((Predicate<String>) String::isEmpty).negate()).orElse(null)))
          .orElse(ofNullable(propertyName(property))
              .orElse(null));
    }
    return propertyName(property);
  }

  private String propertyName(Optional<JacksonXmlProperty> property) {
    if (property.isPresent()) {
      return defaultToNull(ofNullable(property.get().localName())
          .filter(((Predicate<String>) String::isEmpty).negate())
          .orElse(null));
    }
    return null;
  }

  private String defaultToNull(String value) {
    return "##default".equalsIgnoreCase(value) ? null : value;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
