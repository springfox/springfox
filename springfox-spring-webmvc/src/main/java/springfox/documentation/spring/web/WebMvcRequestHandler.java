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
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;
import springfox.documentation.RequestHandlerKey;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;
import springfox.documentation.spring.wrapper.NameValueExpression;
import springfox.documentation.spring.wrapper.PatternsRequestCondition;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import static java.util.Optional.*;

public class WebMvcRequestHandler implements RequestHandler {
  private final String contextPath;
  private final HandlerMethodResolver methodResolver;
  private final RequestMappingInfo requestMapping;
  private final HandlerMethod handlerMethod;

  public WebMvcRequestHandler(
      String contextPath,
      HandlerMethodResolver methodResolver,
      RequestMappingInfo requestMapping,
      HandlerMethod handlerMethod) {
    this.contextPath = contextPath;
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
    return new WebMvcPatternsRequestConditionWrapper(
        contextPath,
        requestMapping.getPatternsCondition());
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
  public Set<MediaType> produces() {
    return requestMapping.getProducesCondition().getProducibleMediaTypes();
  }

  @Override
  public Set<MediaType> consumes() {
    return requestMapping.getConsumesCondition().getConsumableMediaTypes();
  }

  @Override
  public Set<NameValueExpression<String>> headers() {
    return WebMvcNameValueExpressionWrapper.from(requestMapping.getHeadersCondition().getExpressions());
  }

  @Override
  public Set<NameValueExpression<String>> params() {
    return WebMvcNameValueExpressionWrapper.from(requestMapping.getParamsCondition().getExpressions());
  }

  @Override
  public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
    return ofNullable(AnnotationUtils.findAnnotation(handlerMethod.getMethod(), annotation));
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
  public springfox.documentation.spring.wrapper.RequestMappingInfo<?> getRequestMapping() {
    return new WebMvcRequestMappingInfoWrapper(requestMapping);
  }

  @Override
  public List<ResolvedMethodParameter> getParameters() {
    return methodResolver.methodParameters(handlerMethod);
  }

  @Override
  public ResolvedType getReturnType() {
    return methodResolver.methodReturnType(handlerMethod);
  }

  @Override
  public <T extends Annotation> Optional<T> findControllerAnnotation(Class<T> annotation) {
    return ofNullable(AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), annotation));
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", WebMvcRequestHandler.class.getSimpleName() + "{", "}")
        .add("requestMapping=" + requestMapping)
        .add("handlerMethod=" + handlerMethod)
        .add("key=" + key())
        .toString();
  }
}
