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
package springfox.documentation.schema.property;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import org.slf4j.Logger;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.lang.reflect.Method;

import static com.google.common.collect.Lists.*;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class ModelPropertiesKeyGenerator implements KeyGenerator {
  private static final Logger LOG = getLogger(ModelPropertiesKeyGenerator.class);

  @Override
  public Object generate(Object target, Method method, Object... params) {
    Optional<ResolvedType> type = FluentIterable.from(newArrayList(params)).filter(ResolvedType.class).first();
    Optional<ModelContext> context = FluentIterable.from(newArrayList(params)).filter(ModelContext.class).first();
    if (!type.isPresent()) {
      throw new IllegalArgumentException("Key generator can only be used where atleast one parameter is of type "
          + "ResolvedType");
    }
    StringBuilder sb = new StringBuilder();
    sb.append(type.get().toString());
    sb.append(context.transform(returnTypeComponent()).or(""));
    LOG.info("Cache key generated: {}", sb.toString());
    return sb.toString();
  }

  private Function<ModelContext, String> returnTypeComponent() {
    return new Function<ModelContext, String>() {
      @Override
      public String apply(ModelContext input) {
        return String.format("(%s)", input.isReturnType());
      }
    };
  }
}
