package com.mangofactory.swagger.authorization;

import com.mangofactory.swagger.scanners.RegexRequestMappingPatternMatcher;
import com.mangofactory.swagger.scanners.RequestMappingPatternMatcher;
import com.wordnik.swagger.model.Authorization;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.List;

import static com.mangofactory.swagger.ScalaUtils.emptyScalaList;
import static com.mangofactory.swagger.ScalaUtils.toScalaList;

/**
 * A class to represent a default set of authorizations to apply toe each api operation
 * To customize which request mappings/operations the list of authorizations are applied to
 * Specify the custom includePatterns or requestMethods
 */
public class AuthorizationContext {

   private final List<Authorization> authorizations;
   private final RequestMappingPatternMatcher requestMappingPatternMatcher;
   private final List<String> includePatterns;
   private final RequestMethod[] requestMethods;

   private AuthorizationContext(AuthorizationContextBuilder builder) {
      this.authorizations = builder.authorizations;
      this.includePatterns = builder.includePatterns;
      this.requestMappingPatternMatcher = builder.requestMappingPatternMatcher;
      this.requestMethods = builder.requestMethods;
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

   public scala.collection.immutable.List<Authorization> getScalaAuthorizations() {
      if (!CollectionUtils.isEmpty(authorizations)) {
         return toScalaList(authorizations);
      }
      return emptyScalaList();
   }

   public static class AuthorizationContextBuilder {

      private List<Authorization> authorizations;
      private RequestMappingPatternMatcher requestMappingPatternMatcher = new RegexRequestMappingPatternMatcher();
      private List<String> includePatterns = Arrays.asList(new String[]{".*?"});
      private RequestMethod[] requestMethods = RequestMethod.values();

      public AuthorizationContextBuilder(List<Authorization> authorizations) {
         this.authorizations = authorizations;
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
         this.requestMethods = requestMethods;
         return this;
      }

      public AuthorizationContext build() {
         return new AuthorizationContext(this);
      }
   }
}
