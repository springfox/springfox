/*
 *
 *  Copyright 2015-2018 the original author or authors.
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

package springfox.documentation.spi.service.contexts;


import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedField;
import com.google.common.base.Optional;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterMetadataAccessor;

import java.lang.annotation.Annotation;

public class ParameterExpansionContext {

  private final String dataTypeName;
  private final String parentName;
  private final ParameterMetadataAccessor metadataAccessor;
  private final DocumentationType documentationType;
  private final ParameterBuilder parameterBuilder;

  public ParameterExpansionContext(
      String dataTypeName,
      String parentName,
      ParameterMetadataAccessor metadataAccessor,
      DocumentationType documentationType,
      ParameterBuilder parameterBuilder) {

    this.dataTypeName = dataTypeName;
    this.parentName = parentName;
    this.metadataAccessor = metadataAccessor;
    this.documentationType = documentationType;
    this.parameterBuilder = parameterBuilder;
  }

  public String getDataTypeName() {
    return dataTypeName;
  }

  public String getParentName() {
    return parentName;
  }

  /**
   * Access to the raw field is deprecated to support interface based model attributes with resolvers e.g. Pageable
   * @deprecated @since 2.8.0
   * @return resolved field
   */
  @Deprecated
  public ResolvedField getField() {
    throw new UnsupportedOperationException();
  }

  public DocumentationType getDocumentationType() {
    return documentationType;
  }

  public ParameterBuilder getParameterBuilder() {
    return parameterBuilder;
  }

  public ResolvedType getFieldType() {
    return metadataAccessor.getFieldType();
  }

  public String getFieldName() {
    return metadataAccessor.getFieldName();
  }

  public <A extends Annotation> Optional<A> findAnnotation(Class<A> annotationType) {
    return metadataAccessor.findAnnotation(annotationType);
  }
}
