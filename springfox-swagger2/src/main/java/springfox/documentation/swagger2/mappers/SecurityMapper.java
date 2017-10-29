/*
 *
 *  Copyright 2015-2018 the original author or authors.
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
package springfox.documentation.swagger2.mappers;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;

import io.swagger.models.auth.SecuritySchemeDefinition;
import springfox.documentation.schema.Maps;
import springfox.documentation.service.ResourceListing;
import springfox.documentation.service.SecurityScheme;


@Mapper
public class SecurityMapper {
  private static Map<String, SecuritySchemeFactory> factories = new HashMap<String, SecuritySchemeFactory>();
  static {
    factories.put("oauth2", new OAuth2AuthFactory());
    factories.put("apiKey", new ApiKeyAuthFactory());
    factories.put("basicAuth", new BasicAuthFactory());
  };

  public Map<String, SecuritySchemeDefinition> toSecuritySchemeDefinitions(ResourceListing from) {
    if (from == null) {
      return new HashMap<>();
    }
    TreeMap<String, SecuritySchemeDefinition> result = new TreeMap<>();
    Map<String, SecuritySchemeDefinition> bySchemeName = Maps.uniqueIndex(from.getSecuritySchemes(), schemeName())
        .entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> toSecuritySchemeDefinition(e.getValue())));
    result.putAll(bySchemeName);
    return result;
  }

  private Function<SecurityScheme, String> schemeName() {
    return new Function<SecurityScheme, String>() {
      @Override
      public String apply(SecurityScheme input) {
        return input.getName();
      }
    };
  }

  private SecuritySchemeDefinition toSecuritySchemeDefinition(SecurityScheme input) {
    return factories.get(input.getType()).create(input);
  }
}
