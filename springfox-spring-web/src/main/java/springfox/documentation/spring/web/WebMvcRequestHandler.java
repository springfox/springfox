/*
 *
 *  Copyright 2016-2017 the original author or authors.
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
package springfox.documentation.spring.web;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
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
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

public class WebMvcRequestHandler implements RequestHandler {
  private final HandlerMethodResolver methodResolver;
  private final RequestMappingInfo requestMapping;
  private final HandlerMethod handlerMethod;

  public WebMvcRequestHandler(
      HandlerMethodResolver methodResolver,
      RequestMappingInfo requestMapping,
      HandlerMethod handlerMethod) {
    this.methodResolver = methodResolver;
    this.requestMapping = requestMapping;
    this.handlerMethod = handlerMethod;
  }

  @Override
  public HandlerMethod getHandlerMethod() {
    return handlerMethod;
  }

  @Override
  public RequestHandler combine(RequestHandler other) {
    return this;
  }

  @Override
  public Class<?> declaringClass() {
    return handlerMethod.getBeanType();
  }

  @Override
  public boolean isAnnotatedWith(Class<? extends Annotation> annotation) {
    return null != AnnotationUtils.findAnnotation(handlerMethod.getMethod(), annotation);
  }

  @Override
  public PatternsRequestCondition getPatternsCondition() {
    return requestMapping.getPatternsCondition();
  }

  @Override
  public String groupName() {
    return ControllerNamingUtils.controllerNameAsGroup(handlerMethod);
  }

  @Override
  public String getName() {
    return handlerMethod.getMethod().getName();
  }

  @Override
  public Set<RequestMethod> supportedMethods() {
    return requestMapping.getMethodsCondition().getMethods();
  }

  @Override
  public Set<? extends MediaType> produces() {
    return requestMapping.getProducesCondition().getProducibleMediaTypes();
  }

  @Override
  public Set<? extends MediaType> consumes() {
    return requestMapping.getConsumesCondition().getConsumableMediaTypes();
  }

  @Override
  public Set<NameValueExpression<String>> headers() {
    return requestMapping.getHeadersCondition().getExpressions();
  }

  @Override
  public Set<NameValueExpression<String>> params() {
    return requestMapping.getParamsCondition().getExpressions();
  }

  @Override
  public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
    return Optional.fromNullable(AnnotationUtils.findAnnotation(handlerMethod.getMethod(), annotation));
  }

  @Override
  public RequestHandlerKey key() {
    return new RequestHandlerKey(
        requestMapping.getPatternsCondition().getPatterns(),
        requestMapping.getMethodsCondition().getMethods(),
        requestMapping.getConsumesCondition().getConsumableMediaTypes(),
        requestMapping.getProducesCondition().getProducibleMediaTypes());
  }

  @Override
  public List<ResolvedMethodParameter> getParameters(){
    return methodResolver.methodParameters(handlerMethod);
  }

  @Override
  public ResolvedType getReturnType() {
    return methodResolver.methodReturnType(handlerMethod);
  }

  @Override
  public <T extends Annotation> Optional<T> findControllerAnnotation(Class<T> annotation) {
    return Optional.fromNullable(AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), annotation));
  }

  @Override
  public RequestMappingInfo getRequestMapping() {
    return requestMapping;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("WebMvcRequestHandler{");
    sb.append("key=").append(key());
    sb.append('}');
    return sb.toString();
  }
}
