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
import org.springframework.aop.support.AopUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.service.ResolvedMethodParameter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.*;

class ActionSpecification {
  private final Collection<RequestMethod> supportedMethods;
  private final Set<MediaType> produces;
  private final Set<MediaType> consumes;
  private final List<ResolvedMethodParameter> parameters;
  private final ResolvedType returnType;
  private final HandlerMethod handlerMethod;
  private final Class<?> entityType;
  private final String name;
  private final String path;

  @SuppressWarnings("ParameterNumber")
  ActionSpecification(
      String name,
      String path,
      Collection<RequestMethod> supportedMethods,
      Set<MediaType> produces,
      Set<MediaType> consumes,
      HandlerMethod handlerMethod,
      Class<?> entityType,
      List<ResolvedMethodParameter> parameters,
      ResolvedType returnType) {
    this.name = name;
    this.path = path;
    this.supportedMethods = supportedMethods;
    this.produces = produces;
    this.consumes = consumes;
    this.parameters = parameters;
    this.returnType = returnType;
    this.handlerMethod = handlerMethod;
    this.entityType = entityType;
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

  public Collection<RequestMethod> getSupportedMethods() {
    return supportedMethods;
  }

  public Set<MediaType> getProduces() {
    return produces;
  }

  public Set<MediaType> getConsumes() {
    return consumes;
  }

  public List<ResolvedMethodParameter> getParameters() {
    return parameters;
  }

  public ResolvedType getReturnType() {
    return returnType;
  }

  public Optional<HandlerMethod> getHandlerMethod() {
    return ofNullable(handlerMethod);
  }

  public Optional<Class<?>> getDeclaringClass() {
    return Optional.ofNullable(getHandlerMethod().map(input -> {
      Object bean = new OptionalDeferencer<>().convert(handlerMethod.getBean());
      if (AopUtils.isAopProxy(bean)) {
        return AopUtils.getTargetClass(bean);
      }
      return (Class<?>) bean;
    }).orElse(entityType));
  }
}
