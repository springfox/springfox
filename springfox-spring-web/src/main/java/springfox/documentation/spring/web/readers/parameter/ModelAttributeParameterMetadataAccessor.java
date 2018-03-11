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
import com.fasterxml.classmate.members.ResolvedField;
import com.google.common.base.Optional;
import org.springframework.core.annotation.AnnotationUtils;
import springfox.documentation.spi.service.ParameterMetadataAccessor;

import java.lang.annotation.Annotation;

public class ModelAttributeParameterMetadataAccessor implements ParameterMetadataAccessor {
  private final ResolvedField field;
  private final ResolvedType fieldType;
  private final String fieldName;

  public ModelAttributeParameterMetadataAccessor(
      ResolvedField field,
      ResolvedType fieldType,
      String fieldName) {
    this.field = field;
    this.fieldType = fieldType;
    this.fieldName = fieldName;
  }

  public ResolvedField getField() {
    return field;
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
    return Optional.fromNullable(AnnotationUtils.findAnnotation(field.getRawMember(), annotationType));
  }
}
