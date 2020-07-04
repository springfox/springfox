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
import org.springframework.core.MethodParameter;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;

import java.util.Set;

import static springfox.documentation.builders.BuilderDefaults.*;

public class ParameterContext {
  @SuppressWarnings("deprecation")
  private final springfox.documentation.builders.ParameterBuilder parameterBuilder;
  private final ResolvedMethodParameter resolvedMethodParameter;
  private final DocumentationContext documentationContext;
  private final GenericTypeNamingStrategy genericNamingStrategy;
  private final OperationContext operationContext;
  private final RequestParameterBuilder requestParameterBuilder;

  @SuppressWarnings("deprecation")
  public ParameterContext(
      ResolvedMethodParameter resolvedMethodParameter,
      DocumentationContext documentationContext,
      GenericTypeNamingStrategy genericNamingStrategy,
      OperationContext operationContext,
      int parameterIndex) {
    this.parameterBuilder = new springfox.documentation.builders.ParameterBuilder();
    this.requestParameterBuilder = new RequestParameterBuilder()
        .accepts(nullToEmptyList(operationContext.consumes()))
        .parameterIndex(parameterIndex);
    this.resolvedMethodParameter = resolvedMethodParameter;
    this.documentationContext = documentationContext;
    this.genericNamingStrategy = genericNamingStrategy;
    this.operationContext = operationContext;
  }

  public ResolvedMethodParameter resolvedMethodParameter() {
    return resolvedMethodParameter;
  }

  /**
   * @return method parameter
   * @since 2.5.0 this has been deprecated
   * @deprecated Use {@link ParameterContext#resolvedMethodParameter()} instead
   */
  @Deprecated
  public MethodParameter methodParameter() {
    throw new UnsupportedOperationException("Please use resolvedMethodParameter instead");
  }

  /**
   * @return this
   * @since 3.0.0 this has been deprecated in favor of @see
   * @deprecated
   */
  @Deprecated
  public springfox.documentation.builders.ParameterBuilder parameterBuilder() {
    return parameterBuilder;
  }

  public RequestParameterBuilder requestParameterBuilder() {
    return requestParameterBuilder;
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

  public Set<Class> getIgnorableParameterTypes() {
    return documentationContext.getIgnorableParameterTypes();
  }

  public String getGroupName() {
    return operationContext.getGroupName();
  }
}
