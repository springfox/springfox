/*
 *
 *  Copyright 2017-2018 the original author or authors.
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
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.springframework.aop.support.AopUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.service.ResolvedMethodParameter;

import java.util.Collection;
import java.util.List;
import java.util.Set;

class ActionSpecification {
  private final Collection<RequestMethod> supportedMethods;
  private final Set<MediaType> produces;
  private final Set<MediaType> consumes;
  private final List<ResolvedMethodParameter> parameters;
  private final ResolvedType returnType;
  private final HandlerMethod handlerMethod;
  private final String name;
  private final String path;

  public ActionSpecification(
    String name,
    String path,
    Collection<RequestMethod> supportedMethods,
    Set<MediaType> produces,
    Set<MediaType> consumes,
    HandlerMethod handlerMethod,
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
    return Optional.fromNullable(handlerMethod);
  }

  public Optional<Class<?>> getDeclaringClass() {
    return getHandlerMethod().transform(new Function<HandlerMethod, Class<?>>() {
      @Override
      public Class<?> apply(HandlerMethod input) {
        if (AopUtils.isAopProxy(handlerMethod.getBean())) {
          return AopUtils.getTargetClass(handlerMethod.getBean());
        }
        return handlerMethod.getBeanType();
      }
    });
  }
}
