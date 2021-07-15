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
package springfox.documentation.spring.web;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.StringUtils.isEmpty;

public class DescriptionResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(DescriptionResolver.class);

  private final Environment environment;
  private final Map<String, String> cache;

  public DescriptionResolver(Environment environment) {
    this.environment = environment;
    this.cache = new HashMap<>();
  }

  //Thanks to http://stackoverflow.com/a/37962230/19219
  public String resolve(String expression) {
    if (isEmpty(expression)) {
      return expression;
    }

    try {
      expression = environment.resolveRequiredPlaceholders(expression);
    } catch (IllegalArgumentException e) {
      String errorMessage = "Some of the property placeholders in the following expression could not be resolved: '{}'";
      LOGGER.warn(errorMessage, expression);
      expression = environment.resolvePlaceholders(expression);
    }

    // Check if the expression is already been parsed
    if (cache.containsKey(expression)) {
      return cache.get(expression);

    } else {
      // Add to the mapping with key and value as expression
      cache.put(expression, expression);

      // If no match, then return the expression as it is
      return expression;
    }
  }
}
