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

package springfox.documentation.spi.service.contexts;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.http.HttpMethod;
import springfox.documentation.service.SecurityReference;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class SecurityContextBuilder {
  SecurityContextBuilder() {
  }


  private List<SecurityReference> securityReferences = newArrayList();
  private Predicate<String> pathSelector = Predicates.alwaysTrue();
  private Predicate<HttpMethod> methodSelector;

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
      securityReferences = newArrayList();
    }
    if (methodSelector == null) {
      methodSelector = Predicates.alwaysTrue();
    }
    return new SecurityContext(
        securityReferences,
        pathSelector,
        methodSelector);
  }
}
