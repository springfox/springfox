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
package springfox.documentation.spring.web.scanners;

import com.google.common.base.Equivalence;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.service.Operation;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.OperationCachingEquivalence;
import springfox.documentation.spring.web.readers.operation.OperationReader;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Qualifier("cachedOperations")
public class CachingOperationReader implements OperationReader {

  private final LoadingCache<Equivalence.Wrapper<RequestMappingContext>, List<Operation>> cache;

  @Autowired
  public CachingOperationReader(@Qualifier("default") final OperationReader delegate) {
    cache = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(24, TimeUnit.HOURS)
        .build(
            new CacheLoader<Equivalence.Wrapper<RequestMappingContext>, List<Operation>>() {
              public List<Operation> load(Equivalence.Wrapper<RequestMappingContext> key) {
                return delegate.read(key.get());
              }
            });
  }

  @Override
  public List<Operation> read(RequestMappingContext outerContext) {
    return cache.getUnchecked(new OperationCachingEquivalence().wrap(outerContext));
  }
}
