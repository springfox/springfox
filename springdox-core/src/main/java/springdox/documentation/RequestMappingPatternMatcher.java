package springdox.documentation;

import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;

import java.util.Set;

public interface RequestMappingPatternMatcher {
  boolean patternConditionsMatchOneOfIncluded(PatternsRequestCondition patternsCondition,
                                              Set<String> includePatterns);

  boolean pathMatchesOneOfIncluded(String path, Set<String> includePatterns);

}
