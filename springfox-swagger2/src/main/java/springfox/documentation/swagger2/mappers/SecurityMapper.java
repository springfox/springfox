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
package springfox.documentation.swagger2.mappers;

import io.swagger.models.auth.SecuritySchemeDefinition;
import org.mapstruct.Mapper;
import springfox.documentation.service.ResourceListing;
import springfox.documentation.service.SecurityScheme;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;


@Mapper(componentModel = "spring")
public class SecurityMapper {
  private Map<String, SecuritySchemeFactory> factories = unmodifiableMap(Stream.of(
      new AbstractMap.SimpleEntry<>("oauth2", new OAuth2AuthFactory()),
      new AbstractMap.SimpleEntry<>("apiKey", new ApiKeyAuthFactory()),
      new AbstractMap.SimpleEntry<>("basicAuth", new BasicAuthFactory()),
      new AbstractMap.SimpleEntry<>("http", new HttpAuthenticationSchemeFactory()),
      new AbstractMap.SimpleEntry<>("openIdConnect", new OpenIdConnectSchemeFactory()))
      .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));

  public Map<String, SecuritySchemeDefinition> toSecuritySchemeDefinitions(ResourceListing from) {
    if (from == null) {
      return new HashMap<>();
    }
    TreeMap<String, SecuritySchemeDefinition> result
        = new TreeMap<>(from.getSecuritySchemes().stream()
        .collect(toMap(
            SecurityScheme::getName,
            toSecuritySchemeDefinition())));
    return result;
  }

  private Function<SecurityScheme, SecuritySchemeDefinition> toSecuritySchemeDefinition() {
    return input -> factories.get(input.getType()).create(input);
  }
}
