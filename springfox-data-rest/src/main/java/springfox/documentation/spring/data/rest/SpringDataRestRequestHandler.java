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
package springfox.documentation.spring.data.rest;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.RequestHandler;
import springfox.documentation.RequestHandlerKey;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spring.web.WebMvcPatternsRequestConditionWrapper;
import springfox.documentation.spring.web.plugins.CombinedRequestHandler;
import springfox.documentation.spring.wrapper.NameValueExpression;
import springfox.documentation.spring.wrapper.PatternsRequestCondition;
import springfox.documentation.spring.wrapper.RequestMappingInfo;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import static java.util.Optional.*;
import static java.util.stream.Collectors.*;

class SpringDataRestRequestHandler implements RequestHandler {
  private final EntityContext entityContext;
  private final ActionSpecification actionSpecification;
  private final String contextPath;

  SpringDataRestRequestHandler(
      EntityContext entityContext,
      ActionSpecification actionSpecification) {

    this.contextPath = entityContext.contextPath();
    this.entityContext = entityContext;
    this.actionSpecification = actionSpecification;
  }

  @Override
  public Class<?> declaringClass() {
    return actionSpecification.getDeclaringClass().orElse(null);
  }

  @Override
  public boolean isAnnotatedWith(Class<? extends Annotation> annotation) {
    return findAnnotation(annotation).isPresent();
  }

  @Override
  public PatternsRequestCondition getPatternsCondition() {
    return new WebMvcPatternsRequestConditionWrapper(
        contextPath,
        new org.springframework.web.servlet.mvc.condition.PatternsRequestCondition(actionSpecification.getPath())
    );
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
    return actionSpecification.getSupportedMethods().stream()
        .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
  }

  @Override
  public Set<MediaType> produces() {
    return actionSpecification.getProduces();
  }

  @Override
  public Set<MediaType> consumes() {
    return actionSpecification.getConsumes();
  }

  @Override
  public Set<NameValueExpression<String>> headers() {
    return new HashSet<>();
  }

  @Override
  public Set<NameValueExpression<String>> params() {
    return new HashSet<>();
  }

  @Override
  public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
    if (getHandlerMethod() != null) {
      return ofNullable(AnnotationUtils.findAnnotation(getHandlerMethod().getMethod(), annotation));
    }
    return empty();
  }

  @SuppressWarnings("unchecked")
  @Override
  public RequestHandlerKey key() {
    return new RequestHandlerKey(
        getPatternsCondition().getPatterns(),
        supportedMethods(),
        consumes(),
        produces());
  }

  @Override
  public List<ResolvedMethodParameter> getParameters() {
    return new ArrayList<>(actionSpecification.getParameters());
  }

  @Override
  public ResolvedType getReturnType() {
    return actionSpecification.getReturnType();
  }

  @Override
  public <T extends Annotation> Optional<T> findControllerAnnotation(Class<T> annotation) {
    if (getHandlerMethod() != null) {
      return ofNullable(AnnotationUtils.findAnnotation(getHandlerMethod().getBeanType(), annotation));
    }
    return empty();
  }

  @Override
  public HandlerMethod getHandlerMethod() {
    return actionSpecification.getHandlerMethod().orElse(null);
  }

  @Override
  public RequestMappingInfo<?> getRequestMapping() {
    throw new UnsupportedOperationException();
  }

  @Override
  public RequestHandler combine(RequestHandler other) {
    return new CombinedRequestHandler(this, other);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", SpringDataRestRequestHandler.class.getSimpleName() + "{", "}")
        .add("entityContext=" + entityContext)
        .add("actionSpecification=" + actionSpecification)
        .add("key=" + key())
        .toString();
  }
}
