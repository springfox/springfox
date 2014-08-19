package com.mangofactory.swagger.models.alternates;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import java.util.List;

import static com.google.common.collect.Lists.*;


public class AlternateTypeProvider {
  private List<AlternateTypeRule> rules = newArrayList();

  public ResolvedType alternateFor(ResolvedType type) {
    Optional<AlternateTypeRule> matchingRule = FluentIterable.from(rules)
            .firstMatch(thatAppliesTo(type));
    if (matchingRule.isPresent()) {
      return matchingRule.get().alternateFor(type);
    }
    return type;
  }

  private Predicate<AlternateTypeRule> thatAppliesTo(final ResolvedType type) {
    return new Predicate<AlternateTypeRule>() {
      @Override
      public boolean apply(AlternateTypeRule input) {
        return input.appliesTo(type);
      }
    };
  }

  public void addRule(AlternateTypeRule rule) {
    rules.add(rule);
  }
}
