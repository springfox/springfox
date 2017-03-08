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
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.core.mapping.MethodResourceMapping;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.ResourceType;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;
import springfox.documentation.RequestHandlerKey;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spring.web.plugins.CombinedRequestHandler;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Sets.*;
import static springfox.documentation.spring.data.rest.SynthesizedAnnotations.*;
import static springfox.documentation.spring.web.paths.Paths.*;

class EntitySearchRequestHandler implements RequestHandler {
  private final TypeResolver resolver;
  private final RequestMappingInfo requestMapping;
  private final HandlerMethod handlerMethod;
  private final MethodResourceMapping searchResource;
  private final ResourceMetadata resource;
  private final ResourceType resourceType;

  EntitySearchRequestHandler(
      TypeResolver resolver,
      RequestMappingInfo requestMapping,
      HandlerMethod handlerMethod,
      MethodResourceMapping searchResource,
      ResourceMetadata resource) {
    this.resolver = resolver;
    this.requestMapping = requestMapping;
    this.handlerMethod = handlerMethod;
    this.searchResource = searchResource;
    this.resource = resource;
    this.resourceType = resourceType();
  }

  @Override
  public Class<?> declaringClass() {
    return searchResource.getMethod().getDeclaringClass();
  }

  @Override
  public boolean isAnnotatedWith(Class<? extends Annotation> annotation) {
    return null != AnnotationUtils.findAnnotation(searchResource.getMethod(), annotation);
  }

  @Override
  public PatternsRequestCondition getPatternsCondition() {
    Set<String> patterns = newHashSet();
    for (String each : requestMapping.getPatternsCondition().getPatterns()) {
      String replaced = each
          .replace("/{repository}", resource.getPath().toString())
          .replace("/{search}", searchResource.getPath().toString());
      patterns.add(replaced);
    }
    return new PatternsRequestCondition(patterns.toArray(new String[patterns.size()]));
  }

  @Override
  public String groupName() {
    return String.format("%s Entity Search", splitCamelCase(resource.getDomainType().getSimpleName(), ""));
  }

  @Override
  public String getName() {
    if (resourceType == ResourceType.ITEM) {
      return searchResource.getPath().toString();
    }
    return handlerMethod.getMethod().getName();
  }

  @Override
  public Set<RequestMethod> supportedMethods() {
    return requestMapping.getMethodsCondition().getMethods();
  }

  @Override
  public Set<? extends MediaType> produces() {
    ProducesRequestCondition producesCondition = requestMapping.getProducesCondition();
    if (searchResource.getDescription() != null) {
      MediaType type = searchResource.getDescription().getType();
      producesCondition.combine(new ProducesRequestCondition(type.toString()));
    }
    return producesCondition.getProducibleMediaTypes();
  }

  @Override
  public Set<? extends MediaType> consumes() {
    return requestMapping.getConsumesCondition().getConsumableMediaTypes();
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
  public List<ResolvedMethodParameter> getParameters() {
    HandlerMethodResolver handlerMethodResolver = new HandlerMethodResolver(resolver);
    if (resourceType() == ResourceType.ITEM) {
      List<ResolvedMethodParameter> actualQueryParams
          = handlerMethodResolver.methodParameters(
          new HandlerMethod(searchResource.getMethod().getDeclaringClass(), searchResource.getMethod()));
      List<ResolvedMethodParameter> genericParams
          = FluentIterable.from(
          handlerMethodResolver.methodParameters(new HandlerMethod(searchResource, searchResource.getMethod())))
          .filter(maybeFilterSortParam(searchResource.isSortableResource()))
          .filter(maybeFilterPagingParam(searchResource.isPagingResource()))
          .toList();
      return FluentIterable.from(concat(genericParams, actualQueryParams)).toList();
    } else {
      return FluentIterable.from(handlerMethodResolver.methodParameters(handlerMethod))
          .transform(toIgnorable())
          .toList();
    }
  }

  private Function<ResolvedMethodParameter, ResolvedMethodParameter> toIgnorable() {
    return new Function<ResolvedMethodParameter, ResolvedMethodParameter>() {
      @Override
      public ResolvedMethodParameter apply(ResolvedMethodParameter input) {
        return input.annotate(API_IGNORE_ANNOTATION);
      }
    };
  }

  private Predicate<ResolvedMethodParameter> maybeFilterSortParam(final boolean sortableResource) {
    return new Predicate<ResolvedMethodParameter>() {
      @Override
      public boolean apply(ResolvedMethodParameter input) {
        boolean isSortParam = Sort.class.equals(input.getParameterType().getErasedType());
        return (sortableResource && isSortParam) || !isSortParam;
      }
    };
  }

  private Predicate<ResolvedMethodParameter> maybeFilterPagingParam(final boolean pageableResource) {
    return new Predicate<ResolvedMethodParameter>() {
      @Override
      public boolean apply(ResolvedMethodParameter input) {
        boolean isPageableParam = Pageable.class.equals(input.getParameterType().getErasedType());
        return (pageableResource && isPageableParam) || !isPageableParam;
      }
    };
  }

  @Override
  public ResolvedType getReturnType() {
    if (resourceType() == ResourceType.ITEM) {
      MemberResolver memberResolver = new MemberResolver(resolver);
      ResolvedTypeWithMembers members = memberResolver.resolve(
          resolver.resolve(searchResource.getMethod().getDeclaringClass()), null, null);
      for (ResolvedMethod resolvedMethod : members.getMemberMethods()) {
        if (resolvedMethod.getRawMember().equals(searchResource.getMethod())) {
          return resolvedMethod.getReturnType();
        }
      }
      return resolver.resolve(Void.TYPE);
    } else {
      return resolver.resolve(handlerMethod.getReturnType().getParameterType());
    }
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

  ResourceType resourceType() {
    Set<String> patterns = requestMapping.getPatternsCondition().getPatterns();
    if (any(patterns, endsWith("/{search}"))) {
      return ResourceType.ITEM;
    } else {
      return ResourceType.COLLECTION;
    }
  }

  private Predicate<String> endsWith(final String pattern) {
    return new Predicate<String>() {
      @Override
      public boolean apply(String input) {
        return input.contains(pattern);
      }
    };
  }
}
