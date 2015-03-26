package springfox.documentation.spi.schema;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import springfox.documentation.schema.AlternateTypeRule;

import java.util.List;

import static com.google.common.collect.Lists.*;


public class AlternateTypeProvider {
  private List<AlternateTypeRule> rules = newArrayList();

  public AlternateTypeProvider(List<AlternateTypeRule> alternateTypeRules) {
    rules.addAll(alternateTypeRules);
  }

  public ResolvedType alternateFor(ResolvedType type) {
    Optional<AlternateTypeRule> matchingRule = FluentIterable.from(rules)
            .firstMatch(thatAppliesTo(type));
    if (matchingRule.isPresent()) {
      return matchingRule.get().alternateFor(type);
    }
    return type;
  }

  public void addRule(AlternateTypeRule rule) {
    rules.add(rule);
  }

  private Predicate<AlternateTypeRule> thatAppliesTo(final ResolvedType type) {
    return new Predicate<AlternateTypeRule>() {
      @Override
      public boolean apply(AlternateTypeRule input) {
        return input.appliesTo(type);
      }
    };
  }
}
