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

package springfox.documentation.swagger1.dto;

public class AuthorizationCodeGrant extends GrantType {

  private TokenRequestEndpoint tokenRequestEndpoint;
  private TokenEndpoint tokenEndpoint;

  public AuthorizationCodeGrant() {
    super("authorization_code");
  }

  public AuthorizationCodeGrant(TokenRequestEndpoint tokenRequestEndpoint, TokenEndpoint tokenEndpoint) {
    super("authorization_code");
    this.tokenRequestEndpoint = tokenRequestEndpoint;
    this.tokenEndpoint = tokenEndpoint;
  }

  public TokenRequestEndpoint getTokenRequestEndpoint() {
    return tokenRequestEndpoint;
  }

  public void setTokenRequestEndpoint(TokenRequestEndpoint tokenRequestEndpoint) {
    this.tokenRequestEndpoint = tokenRequestEndpoint;
  }

  public TokenEndpoint getTokenEndpoint() {
    return tokenEndpoint;
  }

  public void setTokenEndpoint(TokenEndpoint tokenEndpoint) {
    this.tokenEndpoint = tokenEndpoint;
  }
}
