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
import springfox.documentation.builders.PropertySpecificationBuilder;
import springfox.documentation.spi.DocumentationType;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

public class ModelPropertyContext {
  private final ModelPropertyBuilder builder;
  private final PropertySpecificationBuilder specificationBuilder;
  private final TypeResolver resolver;
  private final BeanPropertyDefinition beanPropertyDefinition;
  private final AnnotatedElement annotatedElement;
  private final DocumentationType documentationType;

  public ModelPropertyContext(
      ModelPropertyBuilder builder,
      PropertySpecificationBuilder specificationBuilder,
      AnnotatedElement annotatedElement,
      TypeResolver resolver,
      DocumentationType documentationType) {
    this(
        builder,
        specificationBuilder,
        resolver,
        null,
        annotatedElement,
        documentationType);

  }

  public ModelPropertyContext(
      ModelPropertyBuilder builder,
      BeanPropertyDefinition beanPropertyDefinition,
      TypeResolver resolver,
      DocumentationType documentationType,
      PropertySpecificationBuilder specificationBuilder) {
    this(
        builder,
        specificationBuilder,
        resolver,
        beanPropertyDefinition,
        null,
        documentationType);
  }

  private ModelPropertyContext(
      ModelPropertyBuilder builder,
      PropertySpecificationBuilder specificationBuilder,
      TypeResolver resolver,
      BeanPropertyDefinition beanPropertyDefinition,
      AnnotatedElement annotatedElement,
      DocumentationType documentationType) {
    this.builder = builder;
    this.specificationBuilder = specificationBuilder;
    this.resolver = resolver;
    this.beanPropertyDefinition = beanPropertyDefinition;
    this.annotatedElement = annotatedElement;
    this.documentationType = documentationType;
  }

  /**
   * Model property build. Use this to override model property attributes
   * @return the builder
   */
  public ModelPropertyBuilder getBuilder() {
    return builder;
  }

  /**
   * Model property specification. Use this to override model property attributes
   * @return the builder
   */
  public PropertySpecificationBuilder getSpecificationBuilder() {
    return specificationBuilder;
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
    return Optional.ofNullable(annotatedElement);
  }

  /**
   * @return bean property definition for this model property
   */
  public Optional<BeanPropertyDefinition> getBeanPropertyDefinition() {
    return Optional.ofNullable(beanPropertyDefinition);
  }

  /**
   * @return resolver used to resolve types
   */
  public TypeResolver getResolver() {
    return resolver;
  }
}
