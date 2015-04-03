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

package springfox.documentation.swagger2.mappers;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition;
import org.mapstruct.Mapper;
import springfox.documentation.service.AuthorizationType;
import springfox.documentation.service.ResourceListing;

import java.util.Map;

import static com.google.common.collect.Maps.*;

@Mapper
public class SecurityMapper {
  private Map<String, SecuritySchemeFactory> factories = ImmutableMap.<String, SecuritySchemeFactory>builder()
          .put("oauth2", new OAuth2AuthFactory())
          .put("apiKey", new ApiKeyAuthFactory())
          .put("basicAuth", new BasicAuthFactory())
          .build();

  public Map<String, SecuritySchemeDefinition> toSecuritySchemeDefinitions(ResourceListing from) {
    return transformValues(uniqueIndex(from.getAuthorizations(), schemeName()), toSecuritySchemeDefinition());
  }

  private Function<AuthorizationType, String> schemeName() {
    return new Function<AuthorizationType, String>() {
      @Override
      public String apply(AuthorizationType input) {
        return input.getName();
      }
    };
  }

  private Function<AuthorizationType, SecuritySchemeDefinition> toSecuritySchemeDefinition() {
    return new Function<AuthorizationType, SecuritySchemeDefinition>() {
      @Override
      public SecuritySchemeDefinition apply(AuthorizationType input) {
        return factories.get(input.getType()).create(input);
      }
    };
  }

}
