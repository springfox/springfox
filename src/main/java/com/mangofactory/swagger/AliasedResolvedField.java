package com.mangofactory.swagger;

import com.fasterxml.classmate.members.ResolvedField;

public class AliasedResolvedField {
    private String alias;
    private ResolvedField resolvedField;

    public AliasedResolvedField(String alias, ResolvedField resolvedField) {
        this.alias = alias;
        this.resolvedField = resolvedField;
    }

    public String getName() {
        return alias;
    }

    public void setName(String alias) {
        this.alias = alias;
    }

    public ResolvedField getResolvedField() {
        return resolvedField;
    }

    public void setResolvedField(ResolvedField resolvedField) {
        this.resolvedField = resolvedField;
    }
}
