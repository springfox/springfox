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
package springfox.documentation.spring.data.rest;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.google.common.base.Optional;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.hateoas.alps.Alps;
import org.springframework.http.HttpEntity;
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
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

class EntitySchemaHandler implements RequestHandler {
  private final TypeResolver resolver;
  private final ResourceMetadata resource;
  private final RequestMappingInfo requestMapping;
  private final HandlerMethod handlerMethod;

  EntitySchemaHandler(
      TypeResolver resolver,
      ResourceMetadata resource,
      RequestMappingInfo requestMapping,
      HandlerMethod handlerMethod) {
    this.resolver = resolver;
    this.resource = resource;
    this.requestMapping = requestMapping;
    this.handlerMethod = handlerMethod;
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
    Set<String> patterns = newHashSet();
    for (String each : requestMapping.getPatternsCondition().getPatterns()) {
      String replaced = each
          .replace("/{repository}", resource.getPath().toString());
      patterns.add(replaced);
    }
    return new PatternsRequestCondition(patterns.toArray(new String[patterns.size()]));
  }

  @Override
  public String groupName() {
    return "Entity Metadata Services";
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
        getPatternsCondition().getPatterns(),
        supportedMethods(),
        produces(),
        consumes());
  }

  @Override
  public List<ResolvedMethodParameter> getParameters() {
    return newArrayList();
  }

  @Override
  public ResolvedType getReturnType() {
    MemberResolver memberResolver = new MemberResolver(resolver);
    ResolvedTypeWithMembers members = memberResolver.resolve(
        resolver.resolve(handlerMethod.getMethod().getDeclaringClass()), null, null);
    for (ResolvedMethod resolvedMethod : members.getMemberMethods()) {
      if (resolvedMethod.getRawMember().equals(handlerMethod.getMethod())) {
        ResolvedType resourceInfo = resolver.resolve(HttpEntity.class, RootResourceInformation.class);
        if (resourceInfo.equals(resolvedMethod.getReturnType())) {
          return resolver.resolve(Alps.class);
        }
        return resolvedMethod.getReturnType();
      }
    }
    return resolver.resolve(Void.TYPE);
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
  public HandlerMethod getHandlerMethod() {
    return handlerMethod;
  }

  @Override
  public RequestHandler combine(RequestHandler other) {
    return new CombinedRequestHandler(this, other);
  }
}
