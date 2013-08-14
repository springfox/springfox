package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.base.Objects;

import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;
import static com.mangofactory.swagger.models.WildcardType.*;

public final class AlternateTypeProcessingRule implements TypeProcessingRule {
    private ResolvedType alternateType;
    private ResolvedType originalType;

    public AlternateTypeProcessingRule(Class<?> originalType, Class<?> alternateType) {
        this.alternateType = asResolvedType(alternateType);
        this.originalType = asResolvedType(originalType);
    }

    public AlternateTypeProcessingRule(ResolvedType originalType, ResolvedType alternateType) {
        this.alternateType = alternateType;
        this.originalType = originalType;
    }

    @Override
    public boolean isIgnorable() {
        return false;
    }

    @Override
    public boolean hasAlternateType() {
        return !Objects.equal(originalType, alternateType);
    }

    @Override
    public ResolvedType originalType() {
        return originalType;
    }

    @Override
    public ResolvedType alternateType(ResolvedType parameterType) {
        if (hasWildcards(originalType)) {
            return replaceWildcardsFrom(collectReplaceables(parameterType, originalType), alternateType);
        } else {
            return alternateType;
        }
    }

    public static AlternateTypeProcessingRule alternate(ResolvedType original, ResolvedType alternate) {
        return new AlternateTypeProcessingRule(original, alternate);
    }

    public static AlternateTypeProcessingRule hashmapAlternate(Class<?> key, Class<?> value) {
        TypeResolver resolver = new TypeResolver();
        return alternate(resolver.resolve(Map.class, key, value),
                resolver.resolve(List.class, resolver.resolve(Entry.class, key, value)));
    }

    @JsonAutoDetect(fieldVisibility = ANY)
    class Entry<K, V> implements Map.Entry<K, V>{
        private K key;
        private V value;

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V v) {
            return value;
        }

    }
}
