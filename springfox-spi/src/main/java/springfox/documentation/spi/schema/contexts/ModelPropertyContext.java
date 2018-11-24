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

package springfox.documentation.spi.schema.contexts;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.spi.DocumentationType;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

import static java.util.Optional.*;

public class ModelPropertyContext {
  private final ModelPropertyBuilder builder;
  private final TypeResolver resolver;
  private final Optional<BeanPropertyDefinition> beanPropertyDefinition;
  private final Optional<AnnotatedElement> annotatedElement;
  private final DocumentationType documentationType;

  public ModelPropertyContext(
      ModelPropertyBuilder builder,
      AnnotatedElement annotatedElement,
      TypeResolver resolver,
      DocumentationType documentationType) {

    this.builder = builder;
    this.resolver = resolver;
    this.annotatedElement = ofNullable(annotatedElement);
    this.beanPropertyDefinition = empty();
    this.documentationType = documentationType;
  }

  public ModelPropertyContext(
      ModelPropertyBuilder builder,
      BeanPropertyDefinition beanPropertyDefinition,
      TypeResolver resolver,
      DocumentationType documentationType) {

    this.builder = builder;
    this.resolver = resolver;
    this.beanPropertyDefinition = ofNullable(beanPropertyDefinition);
    this.documentationType = documentationType;
    annotatedElement = empty();
  }

  /**
   * Model property build. Use this to override model property attributes
   * @return the builder
   */
  public ModelPropertyBuilder getBuilder() {
    return builder;
  }

  /**
   * Documentation type this context supports, swagger 1.2, 2.0 or vanilla spring mvc
   * @return documentation type
   */
  public DocumentationType getDocumentationType() {
    return documentationType;
  }


  /**
   * @return annotated element that this model property is annotated with
   */
  public Optional<AnnotatedElement> getAnnotatedElement() {
    return annotatedElement;
  }

  /**
   * @return bean property definition for this model property
   */
  public Optional<BeanPropertyDefinition> getBeanPropertyDefinition() {
    return beanPropertyDefinition;
  }

  /**
   * @return resolver used to resolve types
   */
  public TypeResolver getResolver() {
    return resolver;
  }
}
