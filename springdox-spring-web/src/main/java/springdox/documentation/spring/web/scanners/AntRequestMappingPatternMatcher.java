package springdox.documentation.spring.web.scanners;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import springdox.documentation.RequestMappingPatternMatcher;

import java.util.Set;

public class AntRequestMappingPatternMatcher implements RequestMappingPatternMatcher {
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
    AntPathMatcher antPathMatcher = new AntPathMatcher();
    for (String includePattern : includePatterns) {
      Assert.notNull(includePattern, "Include patterns should never be null");
      if (antPathMatcher.match(includePattern, path)) {
        return true;
      }
    }
    return false;
  }
}
