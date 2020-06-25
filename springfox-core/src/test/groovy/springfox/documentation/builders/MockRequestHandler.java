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
package springfox.documentation.builders;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;
import springfox.documentation.RequestHandlerKey;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spring.wrapper.PatternsRequestConditionWrapper;
import springfox.documentation.spring.wrapper.NameValueExpression;
import springfox.documentation.spring.wrapper.PatternsRequestCondition;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MockRequestHandler implements RequestHandler {
  private final RequestMappingInfo requestMapping;
  private final HandlerMethod handlerMethod;

  public MockRequestHandler(
      RequestMappingInfo requestMapping,
      HandlerMethod handlerMethod) {
    this.requestMapping = requestMapping;
    this.handlerMethod = handlerMethod;
  }

  public HandlerMethod getHandlerMethod() {
    return handlerMethod;
  }

  @Override
  public RequestHandler combine(RequestHandler other) {
    return this;
  }

  @Override
  public Class<?> declaringClass() {
    return handlerMethod.getMethod().getDeclaringClass();
  }

  @Override
  public boolean isAnnotatedWith(Class<? extends Annotation> annotation) {
    return null != AnnotationUtils.findAnnotation(handlerMethod.getMethod(), annotation);
  }

  @Override
  public PatternsRequestCondition getPatternsCondition() {
    return new PatternsRequestConditionWrapper(requestMapping.getPatternsCondition());
  }

  @Override
  public String groupName() {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public Set<RequestMethod> supportedMethods() {
    return null;
  }

  @Override
  public Set<MediaType> produces() {
    return null;
  }

  @Override
  public Set<MediaType> consumes() {
    return null;
  }

  @Override
  public Set<NameValueExpression<String>> headers() {
    return null;
  }

  @Override
  public Set<NameValueExpression<String>> params() {
    return null;
  }

  @Override
  public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
    return null;
  }

  @Override
  public RequestHandlerKey key() {
    return null;
  }

  @Override
  public List<ResolvedMethodParameter> getParameters() {
    return null;
  }

  @Override
  public ResolvedType getReturnType() {
    return null;
  }

  @Override
  public <T extends Annotation> Optional<T> findControllerAnnotation(Class<T> annotation) {
    return null;
  }

  @Override
  public springfox.documentation.spring.wrapper.RequestMappingInfo<?> getRequestMapping() {
    return null;
  }
}
