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
package springfox.documentation.schema;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.lang.reflect.Method;

import static com.google.common.collect.Lists.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class ModelContextKeyGenerator implements KeyGenerator {
  private static final Logger LOG = getLogger(ModelContextKeyGenerator.class);
  private final TypeResolver resolver;

  @Autowired
  public ModelContextKeyGenerator(TypeResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public Object generate(Object target, Method method, Object... params) {
    Optional<ModelContext> context = FluentIterable.from(newArrayList(params)).filter(ModelContext.class).first();
    if (context.isPresent()) {
      String key = String.format("%s(%s)", context.get().resolvedType(resolver).toString(), context.get().isReturnType());
      LOG.info("Cache Key Generated: {}", key);
      return key;
    }
    throw new IllegalArgumentException("Key generator can only be used where at least one parameter is of type "
        + "ModelContext");
  }
}
