package springdox.documentation.spring.web.scanners;

import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import springdox.documentation.RequestMappingPatternMatcher;

import java.util.Set;

public class RegexRequestMappingPatternMatcher implements RequestMappingPatternMatcher {

  @Override
  public boolean patternConditionsMatchOneOfIncluded(PatternsRequestCondition patternsCondition,
      Set<String> includePatterns) {

    Set<String> patterns = patternsCondition.getPatterns();
    for (String path : patterns) {
      if (pathMatchesOneOfIncluded(path, includePatterns)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean pathMatchesOneOfIncluded(String path, Set<String> includePatterns) {
    for (String includePattern : includePatterns) {
      Assert.notNull(includePattern, "Include patterns should never be null");
      if (path.matches(includePattern)) {
        return true;
      }
    }
    return false;
  }
}
