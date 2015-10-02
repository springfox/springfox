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

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import org.slf4j.Logger;
import org.springframework.cache.interceptor.KeyGenerator;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.readers.operation.ApiOperationReader;

import java.lang.reflect.Method;

import static com.google.common.collect.Lists.*;
import static org.slf4j.LoggerFactory.*;

public class OperationsKeyGenerator implements KeyGenerator {
  private static final Logger LOG = getLogger(OperationsKeyGenerator.class);

  public static String operationKey(RequestMappingContext context) {
    return new OperationsKeyGenerator().generate(ApiOperationReader.class, null, context).toString();
  }

  @Override
  public Object generate(Object target, Method method, Object... params) {
    Optional<RequestMappingContext> context = FluentIterable.from(newArrayList(nullToEmptyArray(params)))
        .filter(RequestMappingContext.class).first();
    if (context.isPresent()) {
      String key = String.format("%s.%s.%s.%s", context.get().getRequestMappingPattern(),
          context.get().getHandlerMethod().getMethod().getDeclaringClass().getName(),
          context.get().getHandlerMethod().getMethod().getName(),
          context.get().getDocumentationContext().getGenericsNamingStrategy().getClass().getSimpleName());
      LOG.info("Cache key generated: {}", key);
      return key;
    }
    throw new IllegalArgumentException("Key generator can only be used where the first Parameter is of type "
        + "RequestMappingContext");
  }

  private Object[] nullToEmptyArray(Object[] params) {
    if (params == null) {
      return new Object[0];
    }
    return params;
  }
}
