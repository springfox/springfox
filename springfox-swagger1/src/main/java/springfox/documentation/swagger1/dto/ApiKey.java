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

public class ApiKey extends AuthorizationType {
  private String keyname;
  private String passAs;

  public ApiKey() {
    super("apiKey");
  }

  public ApiKey(String keyname, String passAs) {
    super("apiKey");
    this.keyname = keyname;
    this.passAs = passAs;
  }

  public ApiKey(String keyname) {
    super("apiKey");
    this.keyname = keyname;
    this.passAs = "header";
  }

  public String getKeyname() {
    return keyname;
  }

  public void setKeyname(String keyname) {
    this.keyname = keyname;
  }

  public String getPassAs() {
    return passAs;
  }

  public void setPassAs(String passAs) {
    this.passAs = passAs;
  }
}
