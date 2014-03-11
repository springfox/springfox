package com.mangofactory.swagger.scanners;

import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;

import java.util.List;

public interface RequestMappingPatternMatcher {
   public boolean  patternConditionsMatchOneOfIncluded(PatternsRequestCondition patternsCondition, List<String> includePatterns);
   public boolean  pathMatchesOneOfIncluded(String path, List<String> includePatterns);

}
