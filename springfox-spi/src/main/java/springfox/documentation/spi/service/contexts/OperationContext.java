/*
 *
 *  Copyright 2015-2016 the original author or authors.
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
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
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

  public List<ResponseMessage> getGlobalResponseMessages(String forHttpMethod) {
    DocumentationContext documentationContext = getDocumentationContext();
    if (documentationContext.getGlobalResponseMessages().containsKey(RequestMethod.valueOf(forHttpMethod))) {
      return documentationContext.getGlobalResponseMessages().get(RequestMethod.valueOf(forHttpMethod));
    }
    return newArrayList();
  }

  public List<Parameter> getGlobalOperationParameters() {
    return nullToEmptyList(getDocumentationContext().getGlobalRequestParameters());
  }

  public List<SecurityContext> securityContext() {
    return FluentIterable.from(getDocumentationContext().getSecurityContexts())
        .filter(pathMatches())
        .toList();
  }

  private Predicate<SecurityContext> pathMatches() {
    return new Predicate<SecurityContext>() {
      @Override
      public boolean apply(SecurityContext input) {
        return input.securityForPath(requestMappingPattern()) != null;
      }
    };
  }

  public String requestMappingPattern() {
    return requestContext.getRequestMappingPattern();
  }

  public DocumentationContext getDocumentationContext() {
    return requestContext.getDocumentationContext();
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

  public Set<? extends MediaType> produces() {
    return requestContext.produces();
  }

  public Set<? extends MediaType> consumes() {
    return requestContext.consumes();
  }

  public ImmutableSet<Class> getIgnorableParameterTypes() {
    return ImmutableSet.copyOf(getDocumentationContext().getIgnorableParameterTypes());
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

  public <T extends Annotation> List<T > findAllAnnotations(Class<T> annotation) {
    return requestContext.findAnnotations(annotation);
  }
}
