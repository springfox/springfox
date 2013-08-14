package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.mangofactory.swagger.models.AlternateTypeProcessingRule.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsEqual.*;

public class HashmapAlternateTest {

    private AlternateTypeProcessingRule mapOfStringAndString;
    private AlternateTypeProcessingRule mapOfStringAndWildcard;
    private AlternateTypeProcessingRule mapOfWildcardAndWildcard;
    private AlternateTypeProcessingRule mapOfWildcardAndString;
    private TypeResolver typeResolver;

    @Before
    public void setup() {
        mapOfStringAndString = hashmapAlternate(String.class, String.class);
        mapOfWildcardAndString = hashmapAlternate(WildcardType.class, String.class);
        mapOfStringAndWildcard = hashmapAlternate(String.class, WildcardType.class);
        mapOfWildcardAndWildcard = hashmapAlternate(WildcardType.class, WildcardType.class);
        typeResolver = new TypeResolver();
    }

    @Test
    public void withNormalMap() {
        ResolvedType alternate = mapOfStringAndString.alternateType(typeResolver.resolve(Map.class, String.class,
                String.class));
        verifyExpectedType(alternate);
    }

    private void verifyExpectedType(ResolvedType alternate) {
        final ResolvedType expected = typeResolver.resolve(List.class, typeResolver.resolve(AlternateTypeProcessingRule
                .Entry.class, String.class, String.class));
        assertThat(alternate, equalTo(expected));
    }

    @Test
    public void withWildcardValue() {
        ResolvedType alternate = mapOfStringAndWildcard.alternateType(typeResolver.resolve(Map.class, String.class,
                String.class));
        verifyExpectedType(alternate);
    }

    @Test
    public void withWildcardKey() {
        ResolvedType alternate = mapOfWildcardAndString.alternateType(typeResolver.resolve(Map.class, String.class,
                String.class));
        verifyExpectedType(alternate);
    }

    @Test
    public void withWildcardEntry() {
        ResolvedType alternate = mapOfWildcardAndWildcard.alternateType(typeResolver.resolve(Map.class, String.class,
                String.class));
        verifyExpectedType(alternate);
    }
}
