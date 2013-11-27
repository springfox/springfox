package com.mangofactory.swagger.scanners;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;

import java.util.List;
import java.util.Set;

public class AntRequestMappingPatternMatcher implements RequestMappingPatternMatcher {
   @Override
   public boolean patternConditionsMatchOneOfIncluded(PatternsRequestCondition patternsCondition, List<String> includePatterns) {
      Set<String> patterns = patternsCondition.getPatterns();
      AntPathMatcher antPathMatcher = new AntPathMatcher();
      for (String path : patterns) {
         for (String includePattern : includePatterns) {
            Assert.notNull(includePattern, "Include patterns should never be null");
            if (antPathMatcher.match(includePattern, path)) {
               return true;
            }
         }
      }
      return false;
   }
}
