package com.mangofactory.documentation.service;

import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;

import java.util.Set;

public interface RequestMappingPatternMatcher {
  public boolean patternConditionsMatchOneOfIncluded(PatternsRequestCondition patternsCondition,
                                                     Set<String> includePatterns);

  public boolean pathMatchesOneOfIncluded(String path, Set<String> includePatterns);

}
