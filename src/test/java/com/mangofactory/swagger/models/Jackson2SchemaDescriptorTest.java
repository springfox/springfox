package com.mangofactory.swagger.models;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.mangofactory.swagger.AliasedResolvedField;
import com.mangofactory.swagger.SwaggerConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Iterables.*;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class Jackson2SchemaDescriptorTest {
    private final boolean forSerialization;
    private List<AliasedResolvedField> fields;
    private List<ResolvedPropertyInfo> properties;

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
        Jackson2SchemaDescriptor descriptor = new Jackson2SchemaDescriptor(new SwaggerConfiguration("1.1", "/"),
                new ObjectMapper());
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
            assertEquals(2, properties.size());
        } else {
            assertEquals(3, properties.size());
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

    private ResolvedPropertyInfo resolvedProperty(final String property) {
        return find(properties, new Predicate<ResolvedPropertyInfo>() {
            @Override
            public boolean apply(ResolvedPropertyInfo input) {
                return property.equals(input.getName());
            }
        }, null);
    }

    @Test
    public void privateProperty() {
        ResolvedPropertyInfo property = resolvedProperty("privateProperty");
        assertNotNull(property);
    }

    @Test
    public void publicField() {
        ResolvedField field = resolvedField("publicField").getResolvedField();
        assertNotNull(field);
    }


    @Test
    public void ignoredPublicField() {
        AliasedResolvedField field = resolvedField("ignoredPublicField");
        assertNull(field);
    }

    @Test
    public void includedPrivateField() {
        ResolvedField field = resolvedField("includedPrivateField").getResolvedField();
        assertNotNull(field);
    }

    @Test
    public void includedRenamedField() {
        ResolvedField field = resolvedField("otherName").getResolvedField();
        assertNotNull(field);
    }

    @Test
    public void nonExistentField() {
        AliasedResolvedField field = resolvedField("nonExistentField");
        assertNull(field);
    }

    @Test
    public void nonExistentProperty() {
        ResolvedPropertyInfo property = resolvedProperty("nonExistentProperty");
        assertNull(property);
    }

    @Test
    public void jsonIgnoreProperties() {
        ResolvedPropertyInfo property = resolvedProperty("ignoreProperty1");
        assertNotNull(property);
    }

    @Test
    public void jsonIgnorePropertiesSerialization() throws IOException {
        SerializationTest test = new SerializationTest();
        test.setIgnoreProperty1("ignored");
        ObjectMapper mapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        mapper.writeValue(stringWriter, test);
        SerializationTest read = mapper.readValue(stringWriter.toString(), SerializationTest.class);
        assertNull(read.getIgnoreProperty1());
    }
}
