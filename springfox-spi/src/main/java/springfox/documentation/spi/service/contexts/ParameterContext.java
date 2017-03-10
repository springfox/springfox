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
import com.google.common.collect.ImmutableSet;
import org.springframework.core.MethodParameter;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;

public class ParameterContext {
  private final ParameterBuilder parameterBuilder;
  private final ResolvedMethodParameter resolvedMethodParameter;
  private final DocumentationContext documentationContext;
  private final GenericTypeNamingStrategy genericNamingStrategy;
  private final OperationContext operationContext;

  public ParameterContext(
      ResolvedMethodParameter resolvedMethodParameter,
      ParameterBuilder parameterBuilder,
      DocumentationContext documentationContext,
      GenericTypeNamingStrategy genericNamingStrategy,
      OperationContext operationContext) {

    this.parameterBuilder = parameterBuilder;
    this.resolvedMethodParameter = resolvedMethodParameter;
    this.documentationContext = documentationContext;
    this.genericNamingStrategy = genericNamingStrategy;
    this.operationContext = operationContext;
  }

  public ResolvedMethodParameter resolvedMethodParameter() {
    return resolvedMethodParameter;
  }

  /**
   * @return
   * @since 2.5.0 this has been deprecated
   * @deprecated Use {@link ParameterContext#resolvedMethodParameter()} instead
   */
  @Deprecated
  public MethodParameter methodParameter() {
    throw new UnsupportedOperationException("Please use resolvedMethodParameter instead");
  }

  public ParameterBuilder parameterBuilder() {
    return parameterBuilder;
  }

  public DocumentationContext getDocumentationContext() {
    return documentationContext;
  }

  public DocumentationType getDocumentationType() {
    return documentationContext.getDocumentationType();
  }

  public ResolvedType alternateFor(ResolvedType parameterType) {
    return getAlternateTypeProvider().alternateFor(parameterType);
  }

  public AlternateTypeProvider getAlternateTypeProvider() {
    return documentationContext.getAlternateTypeProvider();
  }

  public GenericTypeNamingStrategy getGenericNamingStrategy() {
    return genericNamingStrategy;
  }

  public OperationContext getOperationContext() {
    return operationContext;
  }

  public ImmutableSet<Class> getIgnorableParameterTypes() {
    return documentationContext.getIgnorableParameterTypes();
  }

  public String getGroupName() {
    return operationContext.getGroupName();
  }
}
