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

package springfox.documentation.builders;

import org.springframework.lang.NonNull;
import springfox.documentation.service.AuthorizationCodeGrant;
import springfox.documentation.service.TokenEndpoint;
import springfox.documentation.service.TokenRequestEndpoint;

import java.util.function.Consumer;

public class AuthorizationCodeGrantBuilder {
  private TokenRequestEndpoint tokenRequestEndpoint;
  private TokenEndpoint tokenEndpoint;

  /**
   * Updates token request endpoint
   *
   * @param tokenRequestEndpoint - represents the token request endpoint along with the client id and secret
   * @return this
   * @deprecated @since 3.0.0
   * Prefer fluent builder api {@link AuthorizationCodeGrantBuilder#tokenRequestEndpoint(Consumer)}
   */
  @Deprecated
  public AuthorizationCodeGrantBuilder tokenRequestEndpoint(TokenRequestEndpoint tokenRequestEndpoint) {
    this.tokenRequestEndpoint = BuilderDefaults.defaultIfAbsent(tokenRequestEndpoint, this.tokenRequestEndpoint);
    return this;
  }

  /**
   * Updates token request endpoint
   *
   * @param consumer - represents the token request endpoint along with the client id and secret
   * @return this
   */
  public AuthorizationCodeGrantBuilder tokenRequestEndpoint(@NonNull Consumer<TokenRequestEndpointBuilder> consumer) {
    TokenRequestEndpointBuilder endpoint = new TokenRequestEndpointBuilder();
    consumer.accept(endpoint);
    this.tokenRequestEndpoint = BuilderDefaults.defaultIfAbsent(endpoint.build(), this.tokenRequestEndpoint);
    return this;
  }

  /**
   * Updates token endpoint
   *
   * @param tokenEndpoint - represents the token endpoint along with the token name
   * @return this
   * @deprecated @since 3.0.0
   * Prefer fluent builder api {@link AuthorizationCodeGrantBuilder#tokenEndpoint(Consumer)}
   */
  @Deprecated
  public AuthorizationCodeGrantBuilder tokenEndpoint(TokenEndpoint tokenEndpoint) {
    this.tokenEndpoint = BuilderDefaults.defaultIfAbsent(tokenEndpoint, this.tokenEndpoint);
    return this;
  }

  /**
   * Updates token endpoint
   *
   * @param consumer - represents the token endpoint along with the token name
   * @return this
   */
  public AuthorizationCodeGrantBuilder tokenEndpoint(@NonNull Consumer<TokenEndpointBuilder> consumer) {
    TokenEndpointBuilder endpoint = new TokenEndpointBuilder();
    consumer.accept(endpoint);
    this.tokenEndpoint = BuilderDefaults.defaultIfAbsent(endpoint.build(), this.tokenEndpoint);
    return this;
  }


  public AuthorizationCodeGrant build() {
    return new AuthorizationCodeGrant(tokenRequestEndpoint, tokenEndpoint);
  }
}