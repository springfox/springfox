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

package springfox.documentation.swagger1.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.swagger1.dto.ApiKey;
import springfox.documentation.swagger1.dto.Authorization;
import springfox.documentation.swagger1.dto.AuthorizationCodeGrant;
import springfox.documentation.swagger1.dto.AuthorizationType;
import springfox.documentation.swagger1.dto.BasicAuth;
import springfox.documentation.swagger1.dto.ImplicitGrant;
import springfox.documentation.swagger1.dto.LoginEndpoint;
import springfox.documentation.swagger1.dto.TokenEndpoint;
import springfox.documentation.swagger1.dto.TokenRequestEndpoint;

import java.util.List;

@Mapper
public abstract class AuthorizationTypesMapper {
  public abstract springfox.documentation.swagger1.dto.OAuth toSwaggerOAuth(springfox.documentation.service.OAuth from);

  public abstract BasicAuth toSwaggerBasicAuth(springfox.documentation.service.BasicAuth from);

  public abstract ApiKey toSwaggerApiKey(springfox.documentation.service.ApiKey from);

  public abstract ImplicitGrant toSwaggerImplicitGrant(springfox.documentation.service.ImplicitGrant from);

  public abstract AuthorizationCodeGrant
  toSwaggerAuthorizationCodeGrant(springfox.documentation.service.AuthorizationCodeGrant from);

  public abstract TokenEndpoint toSwaggerTokenEndpoint(springfox.documentation.service.TokenEndpoint from);

  public abstract TokenRequestEndpoint
  toSwaggerTokenRequestEndpoint(springfox.documentation.service.TokenRequestEndpoint from);

  public abstract springfox.documentation.swagger1.dto.AuthorizationScope
  toSwaggerAuthorizationScope(AuthorizationScope from);

  @Mappings({
      @Mapping(target = "type", source = "reference")
  })
  public abstract Authorization toSwaggerSecurityReference(SecurityReference from);

  public abstract LoginEndpoint toSwaggerLoginEndpoint(springfox.documentation.service.LoginEndpoint from);

  public springfox.documentation.swagger1.dto.GrantType toSwaggerGrantType(
          springfox.documentation.service.GrantType from) {

    if (from instanceof springfox.documentation.service.ImplicitGrant) {
      return toSwaggerImplicitGrant((springfox.documentation.service.ImplicitGrant) from);
    } else if (from instanceof springfox.documentation.service.AuthorizationCodeGrant) {
      return toSwaggerAuthorizationCodeGrant(
              (springfox.documentation.service.AuthorizationCodeGrant) from);
    }
    throw new UnsupportedOperationException();
  }


  public AuthorizationType toSwaggerAuthorizationType(
          SecurityScheme from) {

    if (from instanceof springfox.documentation.service.ApiKey) {
      return toSwaggerApiKey((springfox.documentation.service.ApiKey) from);
    } else if (from instanceof springfox.documentation.service.OAuth) {
      return toSwaggerOAuth((springfox.documentation.service.OAuth) from);
    } else if (from instanceof springfox.documentation.service.BasicAuth) {
      return toSwaggerBasicAuth((springfox.documentation.service.BasicAuth) from);
    }
    throw new UnsupportedOperationException();
  }

  //List types
  public abstract List<springfox.documentation.swagger1.dto.AuthorizationScope> toSwaggerAuthorizationScopes(
          List<AuthorizationScope> from);

  public abstract List<springfox.documentation.swagger1.dto.GrantType> toSwaggerGrantTypes(List<springfox
          .documentation.service.GrantType> from);

  public abstract List<AuthorizationType> toSwaggerAuthorizationTypes(
          List<SecurityScheme> from);

}
