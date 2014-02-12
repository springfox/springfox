package com.mangofactory.swagger.scanners;

import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;

import java.util.List;
import java.util.Set;

public class RegexRequestMappingPatternMatcher implements RequestMappingPatternMatcher {

   @Override
   public boolean patternConditionsMatchOneOfIncluded(PatternsRequestCondition patternsCondition, List<String> includePatterns) {
      Set<String> patterns = patternsCondition.getPatterns();
      for (String path : patterns) {
         if(pathMatchesOneOfIncluded(path, includePatterns)){
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean pathMatchesOneOfIncluded(String path, List<String> includePatterns) {
      for (String includePattern : includePatterns) {
         Assert.notNull(includePattern, "Include patterns should never be null");
         if (path.matches(includePattern)) {
            return true;
         }
      }
      return false;
   }
}
