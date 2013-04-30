package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.wordnik.swagger.core.DocumentationSchema;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;
import static org.junit.Assert.*;

public class ComplexTypeTest {
    private Map<String, DocumentationSchema> modelMap;


    class Pet {
        String name;
        int age;
        Category category;
        BigDecimal customType;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public BigDecimal getCustomType() {
            return customType;
        }

        public void setCustomType(BigDecimal customType) {
            this.customType = customType;
        }
    }

    class Category {
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private class BigDecimalSchemaGenerator implements CustomSchemaGenerator {
        @Override
        public boolean supports(ResolvedType type) {
            return BigDecimal.class.equals(type.getErasedType());
        }

        @Override
        public Function<SchemaProvider, MemberVisitor> factory() {
            return new Function<SchemaProvider, MemberVisitor>() {
                @Override
                public MemberVisitor apply(SchemaProvider schemaProvider) {
                    return new MemberVisitor() {
                        @Override
                        public DocumentationSchema schema(MemberInfoSource property) {
                            DocumentationSchema schema = new DocumentationSchema();
                            schema.setName(property.getName());
                            schema.setType("double");
                            schema.setDefault("0.0");
                            schema.setNotes("BigDecimal type");
                            return schema;
                        }
                    };
                }
            };
        }
    }

    @Before
    public void setup() {
        modelMap = newHashMap();
        DocumentationSchemaProvider provider = new DocumentationSchemaProvider(new TypeResolver());
        ArrayList<CustomSchemaGenerator> customSchemaGenerators = new ArrayList<CustomSchemaGenerator>();
        customSchemaGenerators.add(new BigDecimalSchemaGenerator());
        provider.setCustomSchemaGenerators(customSchemaGenerators);
        modelMap = provider.getModelMap(new Model("pet", asResolvedType(Pet.class)));
    }

    @Test
    public void hasExpectedModels() {
        assertEquals(2, modelMap.size());
    }

    @Test
    public void hasAPetModel() {
        assertTrue(modelMap.containsKey("Pet"));
        DocumentationSchema pet = modelMap.get("Pet");
        assertNotNull(pet.getProperties());
        assertEquals(4, pet.getProperties().size());
    }

    @Test
    public void hasACategoryModel() {
        assertTrue(modelMap.containsKey("Category"));
        DocumentationSchema category = modelMap.get("Category");
        assertNotNull(category.getProperties());
        assertEquals(1, category.getProperties().size());
    }

    @Test
    public void schemaHasAStringProperty() {
        DocumentationSchema schema = modelMap.get("Pet");
        assertTrue(schema.getProperties().containsKey("name"));
        DocumentationSchema property = schema.getProperties().get("name");
        assertNotNull(property);
        assertEquals("string", property.getType());
    }
    @Test
    public void schemaHasAIntProperty() {
        DocumentationSchema schema = modelMap.get("Pet");
        assertTrue(schema.getProperties().containsKey("age"));
        DocumentationSchema property = schema.getProperties().get("age");
        assertNotNull(property);
        assertEquals("int", property.getType());
    }

    @Test
    public void schemaHasACategoryProperty() {
        DocumentationSchema schema = modelMap.get("Pet");
        assertTrue(schema.getProperties().containsKey("category"));
        DocumentationSchema property = schema.getProperties().get("category");
        assertNotNull(property);
        assertEquals("Category", property.getType());
    }

    @Test
    public void schemaHasABigDecimalProperty() {
        DocumentationSchema schema = modelMap.get("Pet");
        assertTrue(schema.getProperties().containsKey("customType"));
        DocumentationSchema property = schema.getProperties().get("customType");
        assertNotNull(property);
        assertEquals("double", property.getType());
        assertEquals("customType", property.getName());
        assertEquals("BigDecimal type", property.getNotes());
        assertEquals("0.0", property.getDefault());
    }


    @Test
    public void hasACategoryNameProperty() {
        DocumentationSchema category = modelMap.get("Category");
        assertTrue(category.getProperties().containsKey("name"));
    }
}
