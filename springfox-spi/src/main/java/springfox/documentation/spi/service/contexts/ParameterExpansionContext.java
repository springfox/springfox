/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterMetadataAccessor;

import java.lang.annotation.Annotation;
import java.util.Optional;

@SuppressWarnings("deprecation")
public class ParameterExpansionContext {

  private final String dataTypeName;
  private final String parentName;
  private final String parameterType;
  private final ParameterMetadataAccessor metadataAccessor;
  private final DocumentationType documentationType;
  private final springfox.documentation.builders.ParameterBuilder parameterBuilder;
  private final RequestParameterBuilder requestParameterBuilder;

  public ParameterExpansionContext(
      String dataTypeName,
      String parentName,
      String parameterType,
      ParameterMetadataAccessor metadataAccessor,
      DocumentationType documentationType,
      springfox.documentation.builders.ParameterBuilder parameterBuilder,
      RequestParameterBuilder requestParameterBuilder) {

    this.dataTypeName = dataTypeName;
    this.parentName = parentName;
    this.parameterType = parameterType;
    this.metadataAccessor = metadataAccessor;
    this.documentationType = documentationType;
    this.parameterBuilder = parameterBuilder;
    this.requestParameterBuilder = requestParameterBuilder;
  }

  public String getDataTypeName() {
    return dataTypeName;
  }

  public String getParentName() {
    return parentName;
  }

  public String getParameterType() {
    return parameterType;
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

  public springfox.documentation.builders.ParameterBuilder getParameterBuilder() {
    return parameterBuilder;
  }

  public RequestParameterBuilder getRequestParameterBuilder() {
    return requestParameterBuilder;
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
