package com.mangofactory.swagger.models;

import com.wordnik.swagger.core.DocumentationSchema;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;

public class DocumentationSchemaMatchers {
    public static Matcher<DocumentationSchema> hasProperty(final String name, final String typeName) {
        String description = String.format("Schema should have a property %s of type %s", name, typeName);
        return new CustomTypeSafeMatcher<DocumentationSchema>(description) {
            @Override
            protected boolean matchesSafely(DocumentationSchema documentationSchema) {
                return documentationSchema.properties().containsKey(name) &&
                        documentationSchema.properties().get(name).getType().equals(typeName);
            }
        };
    }
}
