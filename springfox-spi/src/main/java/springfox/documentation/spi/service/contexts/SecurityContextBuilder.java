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

package springfox.documentation.spi.service.contexts;

import org.springframework.http.HttpMethod;
import springfox.documentation.service.SecurityReference;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SecurityContextBuilder {
  private List<SecurityReference> securityReferences = new ArrayList<>();
  private Predicate<String> pathSelector = (each) -> true;
  private Predicate<HttpMethod> methodSelector;

  SecurityContextBuilder() {
  }

  public SecurityContextBuilder securityReferences(
      List<SecurityReference> securityReferences) {
    this.securityReferences = securityReferences;
    return this;
  }

  public SecurityContextBuilder forPaths(Predicate<String> selector) {
    this.pathSelector = selector;
    return this;
  }

  public SecurityContextBuilder forHttpMethods(Predicate<HttpMethod> methodSelector) {
    this.methodSelector = methodSelector;
    return this;
  }

  public SecurityContext build() {
    if (securityReferences == null) {
      securityReferences = new ArrayList<>();
    }
    if (methodSelector == null) {
      methodSelector = (each) -> true;
    }
    return new SecurityContext(
        securityReferences,
        pathSelector,
        methodSelector);
  }
}
