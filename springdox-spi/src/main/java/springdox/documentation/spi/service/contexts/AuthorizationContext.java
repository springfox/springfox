package springdox.documentation.spi.service.contexts;

import com.google.common.base.Preconditions;
import org.springframework.util.CollectionUtils;
import springdox.documentation.RequestMappingPatternMatcher;
import springdox.documentation.service.Authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

/**
 * A class to represent a default set of authorizations to apply to each api operation
 * To customize which request mappings the list of authorizations are applied to Specify the custom includePatterns
 * or requestMethods
 */
public class AuthorizationContext {

  private final List<Authorization> authorizations;
  private final RequestMappingPatternMatcher requestMappingPatternMatcher;
  private final Set<String> includePatterns;

  public AuthorizationContext(List<Authorization> authorizations, RequestMappingPatternMatcher
          requestMappingPatternMatcher, Set<String> includePatterns) {

    this.authorizations = authorizations;
    this.requestMappingPatternMatcher = requestMappingPatternMatcher;
    this.includePatterns = includePatterns;
  }

  public List<Authorization> getAuthorizationsForPath(String path) {
    if (requestMappingPatternMatcher.pathMatchesOneOfIncluded(path, includePatterns)) {
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
    private RequestMappingPatternMatcher requestMappingPatternMatcher;
    private Set<String> includePatterns = newHashSet(".*?");

    public AuthorizationContextBuilder withAuthorizations(List<Authorization> authorizations) {
      this.authorizations = authorizations;
      return this;
    }

    public AuthorizationContextBuilder withRequestMappingPatternMatcher(RequestMappingPatternMatcher matcher) {
      this.requestMappingPatternMatcher = matcher;
      return this;
    }

    public AuthorizationContextBuilder withIncludePatterns(Set<String> includePatterns) {
      this.includePatterns = includePatterns;
      return this;
    }

    public AuthorizationContext build() {
      Preconditions.checkNotNull(requestMappingPatternMatcher, "requestMappingPatternMatcher");
      if (includePatterns == null) {
        includePatterns = newHashSet();
      }
      if (authorizations == null) {
        authorizations = newArrayList();
      }
      return new AuthorizationContext(authorizations, requestMappingPatternMatcher, includePatterns);
    }
  }
}
