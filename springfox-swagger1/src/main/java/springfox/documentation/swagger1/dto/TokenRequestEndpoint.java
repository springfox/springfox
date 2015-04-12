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

public class TokenRequestEndpoint {

  private String url;
  private String clientIdName;
  private String clientSecretName;

  public TokenRequestEndpoint() {
  }

  public TokenRequestEndpoint(String url, String clientIdName, String clientSecretName) {
    this.url = url;
    this.clientIdName = clientIdName;
    this.clientSecretName = clientSecretName;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getClientIdName() {
    return clientIdName;
  }

  public void setClientIdName(String clientIdName) {
    this.clientIdName = clientIdName;
  }

  public String getClientSecretName() {
    return clientSecretName;
  }

  public void setClientSecretName(String clientSecretName) {
    this.clientSecretName = clientSecretName;
  }
}
