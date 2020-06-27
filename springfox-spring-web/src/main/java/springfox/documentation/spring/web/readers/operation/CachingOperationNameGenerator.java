/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
package springfox.documentation.spring.web.readers.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import springfox.documentation.OperationNameGenerator;

import java.util.HashMap;
import java.util.Map;

@Component
public class CachingOperationNameGenerator implements OperationNameGenerator {
  private static final Logger LOG = LoggerFactory.getLogger(CachingOperationNameGenerator.class);
  private final Map<String, Integer> generated = new HashMap<>();

  @Override
  public String startingWith(String prefix) {
    if (generated.containsKey(prefix)) {
      generated.put(prefix, generated.get(prefix) + 1);
      String nextUniqueOperationName = String.format("%s_%s", prefix, generated.get(prefix));
      LOG.debug("Generating unique operation named: {}", nextUniqueOperationName);
      return nextUniqueOperationName;
    } else {
      generated.put(prefix, 0);
      return prefix;
    }
  }
}
