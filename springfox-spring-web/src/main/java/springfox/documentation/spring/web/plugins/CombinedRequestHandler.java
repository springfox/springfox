/*
 *
 *  Copyright 2016-2019 the original author or authors.
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
import springfox.documentation.RequestHandler;
import springfox.documentation.RequestHandlerKey;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spring.wrapper.NameValueExpression;
import springfox.documentation.spring.wrapper.PatternsRequestCondition;
import springfox.documentation.spring.wrapper.RequestMappingInfo;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.Optional.*;
import static java.util.stream.Collectors.*;

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

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public PatternsRequestCondition getPatternsCondition() {
    return first.getPatternsCondition().combine(second.getPatternsCondition());
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
    return Stream.concat(
        first.supportedMethods().stream(),
        second.supportedMethods().stream())
        .collect(toSet());
  }

  @Override
  public Set<MediaType> produces() {
    return Stream.concat(
        ofNullable(first.produces()).orElse(emptySet()).stream(),
        ofNullable(second.produces()).orElse(emptySet()).stream())
        .collect(toSet());
  }

  @Override
  public Set<MediaType> consumes() {
    return Stream.concat(
        ofNullable(first.consumes()).orElse(emptySet()).stream(),
        ofNullable(second.consumes()).orElse(emptySet()).stream())
        .collect(toSet());
  }

  @Override
  public Set<NameValueExpression<String>> headers() {
    return Stream.concat(
        first.headers().stream(),
        second.headers().stream())
        .collect(toSet());
  }

  @Override
  public Set<NameValueExpression<String>> params() {
    return first.params();
  }

  @Override
  public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
    return first.findAnnotation(annotation)
        .map(Optional::of)
        .orElse(second.findAnnotation(annotation));
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
    return first.getParameters();
  }

  @Override
  public ResolvedType getReturnType() {
    return first.getReturnType();
  }

  @Override
  public <T extends Annotation> Optional<T> findControllerAnnotation(Class<T> annotation) {
    return first.findControllerAnnotation(annotation)
        .map(Optional::of)
        .orElse(second.findControllerAnnotation(annotation));
  }

  @Override
  public RequestMappingInfo<?> getRequestMapping() {
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
    return new StringJoiner(", ", CombinedRequestHandler.class.getSimpleName() + "{", "}")
        .add("first=" + first)
        .add("second=" + second)
        .add("combined key=" + key())
        .toString();
  }
}
