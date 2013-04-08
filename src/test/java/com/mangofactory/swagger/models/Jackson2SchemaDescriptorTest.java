package com.mangofactory.swagger.models;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Iterables.*;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class Jackson2SchemaDescriptorTest {
    private final boolean forSerialization;
    private List<ResolvedField> fields;
    private List<ResolvedProperty> properties;

    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, fieldVisibility = JsonAutoDetect
            .Visibility.PUBLIC_ONLY)
    class SerializationTest {
        private String privateField;
        public String publicField;
        @JsonIgnore
        public String ignoredPublicField;
        @JsonProperty
        private String includedPrivateField;

        private String privateProperty;
        private String deserializationProperty;

        SerializationTest() {
        }

        SerializationTest(String privateField, String publicField, String ignoredPublicField,
                          String includedPrivateField, String privateProperty) {
            this.privateField = privateField;
            this.publicField = publicField;
            this.ignoredPublicField = ignoredPublicField;
            this.includedPrivateField = includedPrivateField;
            this.privateProperty = privateProperty;
        }

        public String getPrivateProperty() {
            return privateProperty;
        }

        void setPrivateProperty(String privateProperty) {
            this.privateProperty = privateProperty;
        }

        String getDeserializationProperty() {
            return deserializationProperty;
        }

        @JsonProperty
        void setDeserializationProperty(String deserializationProperty) {
            this.deserializationProperty = deserializationProperty;
        }
    }

    public Jackson2SchemaDescriptorTest(boolean forSerialization) {
        this.forSerialization = forSerialization;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { { true }, { false } };
        return Arrays.asList(data);
    }

    @Before
    public void setUp() throws Exception {
        Jackson2SchemaDescriptor descriptor = new Jackson2SchemaDescriptor(new ObjectMapper());
        TypeResolver typeResolver = new TypeResolver();
        if (forSerialization) {
            fields =  descriptor.serializableFields(typeResolver, typeResolver.resolve(SerializationTest.class));
            properties =  descriptor.serializableProperties(typeResolver, typeResolver.resolve(SerializationTest.class));
        } else {
            fields =  descriptor.deserializableFields(typeResolver, typeResolver.resolve(SerializationTest.class));
            properties =  descriptor.deserializableProperties(typeResolver, typeResolver.resolve(SerializationTest
                    .class));
        }

    }

    @Test
    public void propertyCountIsCorrect() {
        if (forSerialization) {
            assertEquals(1, properties.size());
        } else {
            assertEquals(2, properties.size());
        }
    }

    @Test
    public void fieldCountIsCorrect() {
        assertEquals(2, fields.size());
    }

    private ResolvedField resolvedField(final String fieldName) {
        return find(fields, new Predicate<ResolvedField>() {
                @Override
                public boolean apply(ResolvedField input) {
                    return fieldName.equals(input.getName());
                }
            }, null);
    }

    private ResolvedProperty resolvedProperty(final String property) {
        return find(properties, new Predicate<ResolvedProperty>() {
            @Override
            public boolean apply(ResolvedProperty input) {
                return property.equals(input.getName());
            }
        }, null);
    }

    @Test
    @SneakyThrows
    public void privateProperty() {
        ResolvedProperty property = resolvedProperty("privateProperty");
        assertNotNull(property);
    }

    @Test
    @SneakyThrows
    public void publicField() {
        ResolvedField field = resolvedField("publicField");
        assertNotNull(field);
    }


    @Test
    @SneakyThrows
    public void ignoredPublicField() {
        ResolvedField field = resolvedField("ignoredPublicField");
        assertNull(field);
    }

    @Test
    @SneakyThrows
    public void includedPrivateField() {
        ResolvedField field = resolvedField("includedPrivateField");
        assertNotNull(field);
    }


    @Test
    @SneakyThrows
    public void nonExistentField() {
        ResolvedField field = resolvedField("nonExistentField");
        assertNull(field);
    }

    @Test
    @SneakyThrows
    public void nonExistentProperty() {
        ResolvedProperty property = resolvedProperty("nonExistentProperty");
        assertNull(property);
    }
}
