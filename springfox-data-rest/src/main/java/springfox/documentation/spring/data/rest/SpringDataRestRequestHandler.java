/*
 *
 *  Copyright 2017-2018 the original author or authors.
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
package springfox.documentation.spring.data.rest;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;
import springfox.documentation.RequestHandlerKey;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spring.web.plugins.CombinedRequestHandler;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.*;

class SpringDataRestRequestHandler implements RequestHandler {
  private final EntityContext entityContext;
  private final ActionSpecification actionSpecification;

  SpringDataRestRequestHandler(
      EntityContext entityContext,
      ActionSpecification actionSpecification) {
    this.entityContext = entityContext;
    this.actionSpecification = actionSpecification;
  }

  @Override
  public Class<?> declaringClass() {
    return actionSpecification.getDeclaringClass().orNull();
  }

  @Override
  public boolean isAnnotatedWith(Class<? extends Annotation> annotation) {
    return findAnnotation(annotation).isPresent();
  }

  @Override
  public PatternsRequestCondition getPatternsCondition() {
    return new PatternsRequestCondition(actionSpecification.getPath());
  }

  @Override
  public String groupName() {
    return String.format("%s Entity", entityContext.getName());
  }

  @Override
  public String getName() {
    return actionSpecification.getName();
  }

  @Override
  public Set<RequestMethod> supportedMethods() {
    return ImmutableSet.copyOf(actionSpecification.getSupportedMethods());
  }

  @Override
  public Set<? extends MediaType> produces() {
    return actionSpecification.getProduces();
  }

  @Override
  public Set<? extends MediaType> consumes() {
    return actionSpecification.getConsumes();
  }

  @Override
  public Set<NameValueExpression<String>> headers() {
    return newHashSet();
  }

  @Override
  public Set<NameValueExpression<String>> params() {
    return newHashSet();
  }

  @Override
  public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
    if (getHandlerMethod() != null) {
      return Optional.fromNullable(AnnotationUtils.findAnnotation(getHandlerMethod().getMethod(), annotation));
    }
    return Optional.absent();
  }

  @Override
  public RequestHandlerKey key() {
    return new RequestHandlerKey(getPatternsCondition().getPatterns(), supportedMethods(), consumes(), produces());
  }

  @Override
  public List<ResolvedMethodParameter> getParameters() {
    return new ArrayList<ResolvedMethodParameter>(actionSpecification.getParameters());
  }

  @Override
  public ResolvedType getReturnType() {
    return actionSpecification.getReturnType();
  }

  @SuppressWarnings("Guava")
  @Override
  public <T extends Annotation> Optional<T> findControllerAnnotation(Class<T> annotation) {
    if (getHandlerMethod() != null) {
      return Optional.fromNullable(AnnotationUtils.findAnnotation(getHandlerMethod().getBeanType(), annotation));
    }
    return Optional.absent();
  }

  @Override
  public HandlerMethod getHandlerMethod() {
    return actionSpecification.getHandlerMethod().orNull();
  }

  @Override
  public RequestMappingInfo getRequestMapping() {
    throw new UnsupportedOperationException();
  }

  @Override
  public RequestHandler combine(RequestHandler other) {
    return new CombinedRequestHandler(this, other);
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("SpringDataRestRequestHandler{");
    sb.append("key=").append(key());
    sb.append('}');
    return sb.toString();
  }
}
