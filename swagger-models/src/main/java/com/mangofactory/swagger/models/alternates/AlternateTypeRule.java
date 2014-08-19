package com.mangofactory.swagger.models.alternates;

import com.fasterxml.classmate.ResolvedType;

import static com.mangofactory.swagger.models.alternates.WildcardType.*;

public class AlternateTypeRule {
  private final ResolvedType original;
  private final ResolvedType alternate;

  /**
   * Instantiates a new Alternate type rule.
   *
   * @param original  the original type
   * @param alternate the alternate type
   */
  public AlternateTypeRule(ResolvedType original, ResolvedType alternate) {
    this.original = original;
    this.alternate = alternate;
  }

  /**
   * Provides alternate for supplier type.
   *
   * @param type the type
   * @return the alternate for the type
   */
  public ResolvedType alternateFor(ResolvedType type) {
    if (appliesTo(type)) {
      if (hasWildcards(original)) {
        return replaceWildcardsFrom(collectReplaceables(type, original), alternate);
      } else {
        return alternate;
      }
    }
    return original;
  }

  /**
   * Check if an alternate applies to type.
   *
   * @param type the source
   * @return the boolean
   */
  public boolean appliesTo(ResolvedType type) {
    return (hasWildcards(original) && wildcardMatch(type, original)) || exactMatch(original, type);
  }

}
