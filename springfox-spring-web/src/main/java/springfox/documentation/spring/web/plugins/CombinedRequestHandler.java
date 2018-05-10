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

import com.fasterxml.classmate.ResolvedType;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;
import springfox.documentation.RequestHandlerKey;
import springfox.documentation.service.ResolvedMethodParameter;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

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
    Set<String> patterns = Stream.concat(
        first.getPatternsCondition().getPatterns().stream(),
        second.getPatternsCondition().getPatterns().stream()).collect(toSet());
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
    return Stream.concat(first.supportedMethods().stream(), second.supportedMethods().stream()).collect(toSet());
  }

  @Override
  public Set<? extends MediaType> produces() {
    return Stream.concat(Optional.ofNullable(first.produces()).orElse(emptySet()).stream(),
            Optional.ofNullable(second.produces()).orElse(emptySet()).stream()).collect(toSet());
  }

  @Override
  public Set<? extends MediaType> consumes() {
    return Stream.concat(Optional.ofNullable(first.consumes()).orElse(emptySet()).stream(),
            Optional.ofNullable(second.consumes()).orElse(emptySet()).stream()).collect(toSet());
  }

  @Override
  public Set<NameValueExpression<String>> headers() {
    return Stream.concat(first.headers().stream(), second.headers().stream()).collect(toSet());
  }

  @Override
  public Set<NameValueExpression<String>> params() {
    return first.params();
  }

  @Override
  public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
    return first.findAnnotation(annotation).map(Optional::of).orElse(second.findAnnotation(annotation));
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
    return first.findControllerAnnotation(annotation).map(Optional::of).orElse(second.findControllerAnnotation(annotation)) ;
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

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("CombinedRequestHandler{");
    sb.append("first key=").append(first == null ? "No key" : first.key());
    sb.append("second key=").append(second == null ? "No key" : second.key());
    sb.append("combined key=").append(key());
    sb.append('}');
    return sb.toString();
  }
}
