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
package springfox.documentation.spring.web.readers.parameter;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.core.annotation.AnnotationUtils;
import springfox.documentation.spi.service.ParameterMetadataAccessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.*;

public class ModelAttributeParameterMetadataAccessor implements ParameterMetadataAccessor {
  private final List<AnnotatedElement> annotatedElements;
  private final ResolvedType fieldType;
  private final String fieldName;

  public ModelAttributeParameterMetadataAccessor(
      List<AnnotatedElement> annotatedElements,
      ResolvedType fieldType,
      String fieldName) {
    this.annotatedElements = annotatedElements;
    this.fieldType = fieldType;
    this.fieldName = fieldName;
  }

  @Override
  public ResolvedType getFieldType() {
    return fieldType;
  }

  @Override
  public String getFieldName() {
    return fieldName;
  }

  @Override
  public <A extends Annotation> Optional<A> findAnnotation(Class<A> annotationType) {
    for (AnnotatedElement each: annotatedElements) {
      A annotation = AnnotationUtils.findAnnotation(each, annotationType);
      if (annotation != null) {
        return of(annotation);
      }
    }
    return empty();
  }
}
