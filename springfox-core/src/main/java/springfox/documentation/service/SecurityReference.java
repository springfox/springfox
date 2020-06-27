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

package springfox.documentation.service;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static springfox.documentation.builders.BuilderDefaults.*;

public class SecurityReference {
  private final String reference;
  private final List<AuthorizationScope> scopes;

  public SecurityReference(String reference, AuthorizationScope[] scopes) {
    this.scopes = Stream.of(scopes).collect(toList());
    this.reference = reference;
  }

  public String getReference() {
    return reference;
  }

  public List<AuthorizationScope> getScopes() {
    return scopes;
  }

  public static SecurityReferenceBuilder builder() {
    return new SecurityReferenceBuilder();
  }

  public static class SecurityReferenceBuilder {

    private String reference;
    private AuthorizationScope[] scopes;

    /**
     * Updates the authorization reference for the api.
     *
     * @param reference - Valid values for the authorization types are.
     * @return this
     */
    public SecurityReferenceBuilder reference(String reference) {
      this.reference = defaultIfAbsent(reference, this.reference);
      return this;
    }

    /**
     * Updates the authorization scopes that this authorization applies to.
     *
     * @param scopes - Scopes that this authorization applies to.
     * @return this
     */
    public SecurityReferenceBuilder scopes(AuthorizationScope[] scopes) {
      this.scopes = defaultIfAbsent(scopes, this.scopes);
      return this;
    }

    public SecurityReference build() {
      return new SecurityReference(reference, scopes);
    }
  }
}
