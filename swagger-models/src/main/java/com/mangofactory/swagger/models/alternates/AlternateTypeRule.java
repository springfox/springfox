package com.mangofactory.swagger.models.alternates;

import com.fasterxml.classmate.ResolvedType;

import static com.mangofactory.swagger.models.alternates.WildcardType.*;

class AlternateTypeRule {
    private final ResolvedType original;
    private final ResolvedType alternate;

    public AlternateTypeRule(ResolvedType original, ResolvedType alternate) {
        this.original = original;
        this.alternate = alternate;
    }

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

    public boolean appliesTo(ResolvedType source) {
        return (hasWildcards(original) && wildcardMatch(source, original)) || exactMatch(original, source);
    }

}
