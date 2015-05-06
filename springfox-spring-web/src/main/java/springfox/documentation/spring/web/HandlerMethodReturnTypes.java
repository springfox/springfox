/*
 *
 *  Copyright 2015 the original author or authors.
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
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import java.lang.reflect.Proxy;

public final class HandlerMethodReturnTypes {

  private HandlerMethodReturnTypes() {
    throw new UnsupportedOperationException();
  }

  public static ResolvedType handlerReturnType(TypeResolver resolver, HandlerMethod handlerMethod) {
    Class hostClass = useType(handlerMethod.getBeanType())
            .or(handlerMethod.getMethod().getDeclaringClass());
    return new HandlerMethodResolver(resolver).methodReturnType(handlerMethod.getMethod(), hostClass);
  }

  public static Optional<Class> useType(Class beanType) {
    if (Proxy.class.isAssignableFrom(beanType)) {
      return Optional.absent();
    }
    if (Class.class.getName().equals(beanType.getName())) {
      return Optional.absent();
    }
    return Optional.fromNullable(beanType);
  }
}
