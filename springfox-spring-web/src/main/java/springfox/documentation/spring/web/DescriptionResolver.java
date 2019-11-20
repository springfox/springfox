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


import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.util.StringUtils.*;

public class DescriptionResolver {
  private static final Pattern PATTERN = Pattern.compile("\\Q${\\E(.+?)(:(.*))?\\Q}\\E");
  private final Environment environment;
  private Map<String, String> cache;

  public DescriptionResolver(Environment environment) {
    this.environment = environment;
    this.cache = new HashMap<String, String>();
  }

  //Thanks to http://stackoverflow.com/a/37962230/19219
  public String resolve(String expression) {
    if (isEmpty(expression)) {
      return expression;
    }
    
    // Check if the expression is already been parsed
    if (cache.containsKey(expression)) {
      return cache.get(expression);

    }

    // If the expression does not start with $, then no need to do PATTERN
    if (!expression.startsWith("$")) {

      // Add to the mapping with key and value as expression
      cache.put(expression, expression);

      // If no match, then return the expression as it is
      return expression;

    }

    // Create the matcher
    Matcher matcher = PATTERN.matcher(expression);

    // If the matching is there, then add it to the map and return the value
    if (matcher.find()) {

      // Store the value
      String key = matcher.group(1);
      String defaultValue = matcher.group(3);

      // Get the value
      String value = environment.getProperty(key);

      // Store the value in the setting
      if (value != null) {

        // Store in the map
        cache.put(expression, value);

        // return the value
        return value;

      } else if (defaultValue != null) {
        // use default if value not found
        cache.put(expression, defaultValue);

        return defaultValue;
      }

    }

    // Add to the mapping with key and value as expression
    cache.put(expression, expression);

    // If no match, then return the expression as it is
    return expression;
  }
}
