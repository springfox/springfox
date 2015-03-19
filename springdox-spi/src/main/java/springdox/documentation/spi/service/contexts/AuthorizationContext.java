package springdox.documentation.spi.service.contexts;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.util.CollectionUtils;
import springdox.documentation.service.Authorization;

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
