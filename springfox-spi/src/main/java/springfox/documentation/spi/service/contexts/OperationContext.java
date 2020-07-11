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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.service.Response;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;
import springfox.documentation.spring.wrapper.NameValueExpression;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;
import static springfox.documentation.builders.BuilderDefaults.*;

public class OperationContext {
  private final OperationBuilder operationBuilder;
  private final RequestMethod requestMethod;
  private final RequestMappingContext requestContext;
  private final int operationIndex;

  public OperationContext(
      OperationBuilder operationBuilder,
      RequestMethod requestMethod,
      RequestMappingContext requestContext,
      int operationIndex) {
    this.operationBuilder = operationBuilder;
    this.requestMethod = requestMethod;
    this.requestContext = requestContext;
    this.operationIndex = operationIndex;
  }

  public OperationBuilder operationBuilder() {
    return operationBuilder;
  }

  public HttpMethod httpMethod() {
    return HttpMethod.valueOf(requestMethod.toString());
  }

  public int operationIndex() {
    return operationIndex;
  }

  @SuppressWarnings("deprecation")
  public List<springfox.documentation.service.ResponseMessage> getGlobalResponseMessages(String forHttpMethod) {
    DocumentationContext documentationContext = getDocumentationContext();
    if (documentationContext.getGlobalResponseMessages()
        .containsKey(RequestMethod.valueOf(forHttpMethod))) {
      return documentationContext.getGlobalResponseMessages()
          .get(RequestMethod.valueOf(forHttpMethod));
    }
    return new ArrayList<>();
  }

  /**
   * Use {@link OperationContext#getGlobalRequestParameters()} instead
   *
   * @return List
   * @deprecated @since 3.0
   */
  @Deprecated
  public List<springfox.documentation.service.Parameter> getGlobalOperationParameters() {
    return nullToEmptyList(getDocumentationContext().getGlobalRequestParameters());
  }

  public List<RequestParameter> getGlobalRequestParameters() {
    return nullToEmptyList(getDocumentationContext().getGlobalParameters());
  }

  public List<SecurityContext> securityContext() {
    return getDocumentationContext().getSecurityContexts().stream()
        .filter(pathMatches())
        .collect(toList());
  }

  private Predicate<SecurityContext> pathMatches() {
    return input -> !input.securityForOperation(OperationContext.this).isEmpty();
  }

  public String requestMappingPattern() {
    return requestContext.getRequestMappingPattern();
  }

  public DocumentationContext getDocumentationContext() {
    return requestContext.getDocumentationContext();
  }

  public OperationModelContextsBuilder operationModelsBuilder() {
    return requestContext.operationModelsBuilder();
  }

  @SuppressWarnings("deprecation")
  public Map<String, Set<springfox.documentation.schema.Model>> getKnownModels() {
    return requestContext.getModelMap();
  }

  public DocumentationType getDocumentationType() {
    return getDocumentationContext().getDocumentationType();
  }

  public AlternateTypeProvider getAlternateTypeProvider() {
    return getDocumentationContext().getAlternateTypeProvider();
  }

  public ResolvedType alternateFor(ResolvedType resolved) {
    return getAlternateTypeProvider().alternateFor(resolved);
  }

  public Set<MediaType> produces() {
    return requestContext.produces();
  }

  public Set<MediaType> consumes() {
    return requestContext.consumes();
  }

  @SuppressWarnings("rawtypes")
  public Set<Class> getIgnorableParameterTypes() {
    return getDocumentationContext().getIgnorableParameterTypes().stream()
    .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
  }

  public GenericTypeNamingStrategy getGenericsNamingStrategy() {
    return getDocumentationContext().getGenericsNamingStrategy();
  }

  public Set<NameValueExpression<String>> headers() {
    return requestContext.headers();
  }

  public Set<NameValueExpression<String>> params() {
    return requestContext.params();
  }

  public String getName() {
    return requestContext.getName();
  }

  public String getGroupName() {
    return requestContext.getGroupName();
  }

  public List<ResolvedMethodParameter> getParameters() {
    return requestContext.getParameters();
  }


  public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
    return requestContext.findAnnotation(annotation);
  }

  public ResolvedType getReturnType() {
    return requestContext.getReturnType();
  }

  public <T extends Annotation> Optional<T> findControllerAnnotation(Class<T> annotation) {
    return requestContext.findControllerAnnotation(annotation);
  }

  public <T extends Annotation> List<T> findAllAnnotations(Class<T> annotation) {
    return requestContext.findAnnotations(annotation);
  }

  public Collection<Response> globalResponsesFor(HttpMethod httpMethod) {
    return getDocumentationContext().globalResponsesFor(httpMethod);
  }
}
