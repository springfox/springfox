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


import org.springframework.http.HttpMethod;
import springfox.documentation.service.SecurityReference;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A class to represent a default set of authorizations to apply to each api operation
 * To customize which request mappings the list of authorizations are applied to Specify the custom includePatterns
 * or requestMethods
 */
public class SecurityContext {

  private final List<SecurityReference> securityReferences;
  private final Predicate<String> selector;
  private final Predicate<HttpMethod> methodSelector;

  public SecurityContext(
      List<SecurityReference> securityReferences,
      Predicate<String> selector) {

    this.securityReferences = securityReferences;
    this.selector = selector;
    this.methodSelector = (item) -> true;
  }

  public SecurityContext(
      List<SecurityReference> securityReferences,
      Predicate<String> selector,
      Predicate<HttpMethod> methodSelector) {

    this.securityReferences = securityReferences;
    this.selector = selector;
    this.methodSelector = methodSelector;
  }

  /**
   * Use securityForOperation instead
   * @since 2.8.1
   * @param path path to secure
   * @return list of applicable security references
   * @deprecated {@link SecurityContext#securityForOperation}
   */
  @Deprecated
  public List<SecurityReference> securityForPath(String path) {
    if (selector.test(path)) {
      return securityReferences;
    }
    return new ArrayList<SecurityReference>();
  }

  public List<SecurityReference> securityForOperation(OperationContext operationContext) {
    if (selector.test(operationContext.requestMappingPattern())
        && methodSelector.test(operationContext.httpMethod())) {
      return securityReferences;
    }
    return new ArrayList<SecurityReference>();
  }

  public List<SecurityReference> getSecurityReferences() {
    return securityReferences;
  }

  public Predicate<HttpMethod> getMethodSelector() {
    return methodSelector;
  }

  public static SecurityContextBuilder builder() {
    return new SecurityContextBuilder();
  }
}
