package springdox.documentation.builders;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.util.AntPathMatcher;

public class PathSelectors {
  private PathSelectors() {
    throw new UnsupportedOperationException();
  }

  public static Predicate<String> any() {
    return Predicates.alwaysTrue();
  }

  public static Predicate<String> none() {
    return Predicates.alwaysFalse();
  }

  public static Predicate<String> regex(final String pathRegex) {
    return new Predicate<String>() {
      @Override
      public boolean apply(String input) {
        return input.matches(pathRegex);
      }
    };
  }

  public static Predicate<String> ant(final String antPattern) {
    return new Predicate<String>() {
      @Override
      public boolean apply(String input) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(antPattern, input);
      }
    };
  }
}
