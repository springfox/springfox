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

import io.swagger.models.auth.OAuth2Definition;
import io.swagger.models.auth.SecuritySchemeDefinition;
import springfox.documentation.service.AuthorizationCodeGrant;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.ClientCredentialsGrant;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.ImplicitGrant;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;

class OAuth2AuthFactory implements SecuritySchemeFactory {
  @Override
  public SecuritySchemeDefinition create(SecurityScheme input) {
    OAuth oAuth = (OAuth) input;
    OAuth2Definition definition = new OAuth2Definition();
    for (GrantType each : oAuth.getGrantTypes()) {
      if ("authorization_code".equals(each.getType())) {
        definition.accessCode(((AuthorizationCodeGrant) each).getTokenRequestEndpoint().getUrl(),
                ((AuthorizationCodeGrant) each).getTokenEndpoint().getUrl());
      } else if ("implicit".equals(each.getType())) {
        definition.implicit(((ImplicitGrant) each).getLoginEndpoint().getUrl());
      } else if ("application".equals(each.getType())) {
//          NOTE: swagger 1 doesn't support this
        definition.application(((ClientCredentialsGrant) each).getTokenUrl());
      } else if ("password".equals(each.getType())) {
//          NOTE: swagger 1 doesn't support this
        definition.password(((ResourceOwnerPasswordCredentialsGrant) each).getTokenUrl());
      } else {
        throw new IllegalArgumentException(String.format("Security scheme of type %s not supported",
            input.getClass().getSimpleName()));
      }
    }
    for (AuthorizationScope each : oAuth.getScopes()) {
      definition.addScope(each.getScope(), each.getDescription());
    }
    VendorExtensionsMapper vendorMapper = new VendorExtensionsMapper();
    definition.setVendorExtensions(vendorMapper.mapExtensions(input.getVendorExtensions()));
    return definition;
  }
}
