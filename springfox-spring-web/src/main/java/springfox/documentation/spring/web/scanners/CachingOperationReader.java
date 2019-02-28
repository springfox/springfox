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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.service.Operation;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.OperationCachingEquivalence;
import springfox.documentation.spring.web.readers.operation.OperationReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@Qualifier("cachedOperations")
public class CachingOperationReader implements OperationReader {
  private final Map<OperationCachingEquivalence.Wrapper, List<Operation>> cache;
  private final Function<OperationCachingEquivalence.Wrapper, List<Operation>> lookup;

  @Autowired
  public CachingOperationReader(@Qualifier("default") final OperationReader delegate) {
    cache = new HashMap<>();
    lookup = (key) -> delegate.read(key.get());
  }

  @Override
  public List<Operation> read(RequestMappingContext outerContext) {
    return cache.computeIfAbsent(new OperationCachingEquivalence().wrap(outerContext), lookup);
  }
}
