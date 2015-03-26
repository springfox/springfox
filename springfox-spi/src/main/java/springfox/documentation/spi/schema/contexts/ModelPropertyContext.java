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

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Optional;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.spi.DocumentationType;

import java.lang.reflect.AnnotatedElement;

public class ModelPropertyContext {
  private final ModelPropertyBuilder builder;
  private final Optional<BeanPropertyDefinition> beanPropertyDefinition;
  private final Optional<AnnotatedElement> annotatedElement;
  private final DocumentationType documentationType;

  public ModelPropertyContext(ModelPropertyBuilder builder, AnnotatedElement annotatedElement,
                              DocumentationType documentationType) {
    this.builder = builder;
    this.annotatedElement = Optional.fromNullable(annotatedElement);
    this.beanPropertyDefinition = Optional.absent();
    this.documentationType = documentationType;
  }

  public ModelPropertyContext(ModelPropertyBuilder builder, BeanPropertyDefinition beanPropertyDefinition,
                              DocumentationType documentationType) {

    this.builder = builder;
    this.beanPropertyDefinition = Optional.fromNullable(beanPropertyDefinition);
    this.documentationType = documentationType;
    annotatedElement = Optional.absent();
  }

  public ModelPropertyBuilder getBuilder() {
    return builder;
  }

  public DocumentationType getDocumentationType() {
    return documentationType;
  }


  public Optional<AnnotatedElement> getAnnotatedElement() {
    return annotatedElement;
  }

  public Optional<BeanPropertyDefinition> getBeanPropertyDefinition() {
    return beanPropertyDefinition;
  }
}
