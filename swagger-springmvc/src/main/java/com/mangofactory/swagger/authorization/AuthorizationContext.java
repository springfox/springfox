package com.mangofactory.swagger.authorization;

import com.mangofactory.service.model.Authorization;
import com.mangofactory.swagger.scanners.RegexRequestMappingPatternMatcher;
import com.mangofactory.swagger.scanners.RequestMappingPatternMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.*;

/**
 * A class to represent a default set of authorizations to apply to each api operation
 * To customize which request mappings the list of authorizations are applied to Specify the custom includePatterns
 * or requestMethods
 */
public class AuthorizationContext {

  private final List<Authorization> authorizations;
  private final RequestMappingPatternMatcher requestMappingPatternMatcher;
  private final List<String> includePatterns;

  public AuthorizationContext(List<Authorization> authorizations, RequestMappingPatternMatcher
          requestMappingPatternMatcher, List<String> includePatterns) {

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
    private RequestMappingPatternMatcher requestMappingPatternMatcher = new RegexRequestMappingPatternMatcher();
    private List<String> includePatterns = Arrays.asList(".*?");

    public AuthorizationContextBuilder withAuthorizations(List<Authorization> authorizations) {
      this.authorizations = authorizations;
      return this;
    }

    public AuthorizationContextBuilder withRequestMappingPatternMatcher(RequestMappingPatternMatcher matcher) {
      this.requestMappingPatternMatcher = matcher;
      return this;
    }

    public AuthorizationContextBuilder withIncludePatterns(List<String> includePatterns) {
      this.includePatterns = includePatterns;
      return this;
    }

    public AuthorizationContextBuilder withRequestMethods(RequestMethod[] requestMethods) {
      return this;
    }

    public AuthorizationContext build() {
      return new AuthorizationContext(authorizations,
              requestMappingPatternMatcher, includePatterns);
    }
  }
}
