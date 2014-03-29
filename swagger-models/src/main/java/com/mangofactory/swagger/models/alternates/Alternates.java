package com.mangofactory.swagger.models.alternates;

import com.fasterxml.classmate.TypeResolver;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static com.mangofactory.swagger.models.ResolvedTypes.*;

public class Alternates {

    public static AlternateTypeRule newRule(Type original, Type alternate) {
        TypeResolver resolver = new TypeResolver();
        return new AlternateTypeRule(asResolved(resolver, original), asResolved(resolver, alternate));
    }

    public static AlternateTypeRule hashMapAlternate(Class<?> key, Class<?> value) {
        TypeResolver resolver = new TypeResolver();
        return new AlternateTypeRule(resolver.resolve(Map.class, key, value),
                resolver.resolve(List.class, resolver.resolve(Map.Entry.class, key, value)));
    }
}
