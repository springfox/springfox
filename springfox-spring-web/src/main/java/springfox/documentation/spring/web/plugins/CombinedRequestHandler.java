/*
 *
 *  Copyright 2016 the original author or authors.
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
package springfox.documentation.spring.web.plugins;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import com.fasterxml.classmate.ResolvedType;

import springfox.documentation.RequestHandler;
import springfox.documentation.RequestHandlerKey;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.util.Predicates;

public class CombinedRequestHandler implements RequestHandler {
  private final RequestHandler first;
  private final RequestHandler second;

  public CombinedRequestHandler(RequestHandler first, RequestHandler second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public Class<?> declaringClass() {
    return first.declaringClass();
  }

  @Override
  public boolean isAnnotatedWith(Class<? extends Annotation> annotation) {
    return first.isAnnotatedWith(annotation) || second.isAnnotatedWith(annotation);
  }

  @Override
  public PatternsRequestCondition getPatternsCondition() {
    Set<String> patterns = union(
        first.getPatternsCondition().getPatterns(),
        second.getPatternsCondition().getPatterns());
    return new PatternsRequestCondition(patterns.toArray(new String[patterns.size()]));
  }

  @Override
  public String groupName() {
    return first.groupName();
  }

  @Override
  public String getName() {
    return first.getName();
  }

  @Override
  public Set<RequestMethod> supportedMethods() {
    return union(first.supportedMethods(), second.supportedMethods());
  }

  @Override
  public Set<? extends MediaType> produces() {
    return union(first.produces(), second.produces());
  }

  @Override
  public Set<? extends MediaType> consumes() {
    return union(first.consumes(), second.consumes());
  }

  @Override
  public Set<NameValueExpression<String>> headers() {
    return union(first.headers(), second.headers());
  }

  @Override
  public Set<NameValueExpression<String>> params() {
    return first.params();
  }

  @Override
  public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
    return Predicates.or(first.findAnnotation(annotation), second.findAnnotation(annotation));
  }

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
    return first.getParameters();
  }

  @Override
  public ResolvedType getReturnType() {
    return first.getReturnType();
  }

  @Override
  public <T extends Annotation> Optional<T> findControllerAnnotation(Class<T> annotation) {
    return Predicates.or(first.findControllerAnnotation(annotation), second.findControllerAnnotation(annotation)) ;
  }

  @Override
  public RequestMappingInfo getRequestMapping() {
    return first.getRequestMapping();
  }

  @Override
  public HandlerMethod getHandlerMethod() {
    return second.getHandlerMethod();
  }

  @Override
  public RequestHandler combine(RequestHandler other) {
    return new CombinedRequestHandler(this, other);
  }
  
  private <T> Set<T> union(Set<? extends T> a, Set<? extends T> b) {
    LinkedHashSet<T> u = new LinkedHashSet<>(a);
    u.addAll(b);
    return u;
  }
}
