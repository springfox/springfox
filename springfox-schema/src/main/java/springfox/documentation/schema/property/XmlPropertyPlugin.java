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
package springfox.documentation.schema.property;

import com.google.common.base.Optional;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.JaxbPresentInClassPathCondition;
import springfox.documentation.schema.Xml;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import static com.google.common.base.Strings.*;
import static springfox.documentation.schema.Annotations.*;

@Component
@Conditional(JaxbPresentInClassPathCondition.class)
public class XmlPropertyPlugin implements ModelPropertyBuilderPlugin {
  
  @Override
  public void apply(ModelPropertyContext context) {
    Optional<XmlElement> elementAnnotation = Optional.absent();
    Optional<XmlAttribute> attributeAnnotation = Optional.absent();

    if (context.getAnnotatedElement().isPresent()) {
      elementAnnotation = elementAnnotation.or(findAnnotation(
          context.getAnnotatedElement().get(),
          XmlElement.class));
      attributeAnnotation = attributeAnnotation.or(findAnnotation(
          context.getAnnotatedElement().get(),
          XmlAttribute.class));
    }

    if (context.getBeanPropertyDefinition().isPresent()) {
      elementAnnotation = elementAnnotation.or(findPropertyAnnotation(
          context.getBeanPropertyDefinition().get(),
          XmlElement.class));
      attributeAnnotation = attributeAnnotation.or(findPropertyAnnotation(
          context.getBeanPropertyDefinition().get(),
          XmlAttribute.class));
    }

    if (elementAnnotation.isPresent() && context.getBeanPropertyDefinition().isPresent()) {
      Optional<XmlElementWrapper> wrapper = findPropertyAnnotation(
          context.getBeanPropertyDefinition().get(),
          XmlElementWrapper.class);

      context.getBuilder()
          .xml(new Xml()
              .attribute(false)
              .namespace(defaultToNull(elementAnnotation.get().namespace()))
              .name(wrapperName(wrapper, elementAnnotation))
              .wrapped(wrapper.isPresent()));
    } else if (attributeAnnotation.isPresent()) {
      context.getBuilder()
          .xml(new Xml()
              .attribute(true)
              .namespace(defaultToNull(attributeAnnotation.get().namespace()))
              .name(attributeName(attributeAnnotation))
              .wrapped(false));
    }
  }

  public static <T extends Annotation> Optional<T> findAnnotation(
      AnnotatedElement annotated,
      Class<T> annotation) {
    return Optional.fromNullable(AnnotationUtils.getAnnotation(annotated, annotation));
  }

  private String wrapperName(Optional<XmlElementWrapper> wrapper, Optional<XmlElement> element) {
    if (wrapper.isPresent()) {
      return Optional.fromNullable(defaultToNull(emptyToNull(wrapper.get().name())))
          .or(Optional.fromNullable(elementName(element)))
          .orNull();
    }
    return elementName(element);
  }

  private String elementName(Optional<XmlElement> element) {
    if (element.isPresent()) {
      return defaultToNull(emptyToNull(element.get().name()));
    }
    return null;
  }

  private String attributeName(Optional<XmlAttribute> attribute) {
    if (attribute.isPresent()) {
      return defaultToNull(emptyToNull(attribute.get().name()));
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
