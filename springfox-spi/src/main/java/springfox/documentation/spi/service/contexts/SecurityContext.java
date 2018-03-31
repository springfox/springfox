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
import org.springframework.http.HttpMethod;
import springfox.documentation.service.SecurityReference;

import java.util.List;

import static com.google.common.base.Predicates.*;

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
    this.methodSelector = alwaysTrue();
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
   * Use securitForOperation instead
   * @since 2.8.1
   * @param path
   * @return list of applicable security references
   * @deprecated {@see SecurityContext#securityForOperation}
   */
  @Deprecated
  public List<SecurityReference> securityForPath(String path) {
    if (selector.apply(path)) {
      return securityReferences;
    }
    return null;
  }

  public List<SecurityReference> securityForOperation(OperationContext operationContext) {
    if (selector.apply(operationContext.requestMappingPattern())
        && methodSelector.apply(operationContext.httpMethod())) {
      return securityReferences;
    }
    return null;
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
