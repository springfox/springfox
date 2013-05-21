package com.mangofactory.swagger.models;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.mangofactory.swagger.AliasedResolvedField;
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
    private List<AliasedResolvedField> fields;
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

//        @JsonProperty
        @JsonProperty(value = "otherName")
        private String renamedPrivateField;

        private String privateProperty;
        private String deserializationProperty;

        SerializationTest() {
        }

        SerializationTest(String privateField, String publicField, String ignoredPublicField,
                          String includedPrivateField, String privateProperty, String renamedPrivateField) {
            this.privateField = privateField;
            this.publicField = publicField;
            this.ignoredPublicField = ignoredPublicField;
            this.includedPrivateField = includedPrivateField;
            this.privateProperty = privateProperty;
            this.renamedPrivateField = renamedPrivateField;
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
        assertEquals(3, fields.size());
    }

    private AliasedResolvedField resolvedField(final String fieldName) {
        return find(fields, new Predicate<AliasedResolvedField>() {
                @Override
                public boolean apply(AliasedResolvedField input) {
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
        ResolvedField field = resolvedField("publicField").getResolvedField();
        assertNotNull(field);
    }


    @Test
    @SneakyThrows
    public void ignoredPublicField() {
        AliasedResolvedField field = resolvedField("ignoredPublicField");
        assertNull(field);
    }

    @Test
    @SneakyThrows
    public void includedPrivateField() {
        ResolvedField field = resolvedField("includedPrivateField").getResolvedField();
        assertNotNull(field);
    }

    @Test
    @SneakyThrows
    public void includedRenamedField() {
        ResolvedField field = resolvedField("otherName").getResolvedField();
        assertNotNull(field);
    }

    @Test
    @SneakyThrows
    public void nonExistentField() {
        AliasedResolvedField field = resolvedField("nonExistentField");
        assertNull(field);
    }

    @Test
    @SneakyThrows
    public void nonExistentProperty() {
        ResolvedProperty property = resolvedProperty("nonExistentProperty");
        assertNull(property);
    }
}
