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
import org.springframework.util.CollectionUtils;
import springfox.documentation.service.Authorization;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.*;

/**
 * A class to represent a default set of authorizations to apply to each api operation
 * To customize which request mappings the list of authorizations are applied to Specify the custom includePatterns
 * or requestMethods
 */
public class AuthorizationContext {

  private final List<Authorization> authorizations;
  private final Predicate<String> selector;

  public AuthorizationContext(List<Authorization> authorizations, Predicate<String> selector) {

    this.authorizations = authorizations;
    this.selector = selector;
  }

  public List<Authorization> getAuthorizationsForPath(String path) {
    if (selector.apply(path)) {
      return authorizations;
    }
    return null;
  }

  public List<Authorization> getAuthorizations() {
    return authorizations;
  }

  public List<Authorization> getScalaAuthorizations() {
    return CollectionUtils.isEmpty(authorizations) ? new ArrayList<Authorization>() : this.authorizations;
  }

  public static AuthorizationContextBuilder builder() {
    return new AuthorizationContextBuilder();
  }

  public static class AuthorizationContextBuilder {

    private List<Authorization> authorizations = newArrayList();
    private Predicate<String> pathSelector = Predicates.alwaysTrue();

    public AuthorizationContextBuilder withAuthorizations(List<Authorization> authorizations) {
      this.authorizations = authorizations;
      return this;
    }

    public AuthorizationContext build() {
      if (authorizations == null) {
        authorizations = newArrayList();
      }
      return new AuthorizationContext(authorizations, pathSelector);
    }

    public AuthorizationContextBuilder forPaths(Predicate<String> selector) {
      this.pathSelector = selector;
      return this;
    }
  }
}
