package com.mangofactory.swagger.models;

import com.wordnik.swagger.core.DocumentationSchema;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.google.common.collect.Maps.*;
import static org.junit.Assert.*;

public class ComplexTypeTest {
    private Map<String, DocumentationSchema> modelMap;

    class Pet {
        String name;
        int age;
        Category category;

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

    @Before
    public void setup() {
        modelMap = newHashMap();
        DocumentationSchemaProvider provider = new DocumentationSchemaProvider();
        modelMap = provider.getModelMap(new Model("pet", Pet.class));
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
        assertEquals(3, pet.getProperties().size());
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
        DocumentationSchema stringProperty = schema.getProperties().get("name");
        assertNotNull(stringProperty);
        assertEquals("string", stringProperty.getType());
    }

    @Test
    public void schemaHasAIntProperty() {
        DocumentationSchema schema = modelMap.get("Pet");
        assertTrue(schema.getProperties().containsKey("age"));
        DocumentationSchema stringProperty = schema.getProperties().get("age");
        assertNotNull(stringProperty);
        assertEquals("int", stringProperty.getType());
    }
    @Test
    public void schemaHasACategoryProperty() {
        DocumentationSchema schema = modelMap.get("Pet");
        assertTrue(schema.getProperties().containsKey("category"));
        DocumentationSchema stringProperty = schema.getProperties().get("category");
        assertNotNull(stringProperty);
        assertEquals("Category", stringProperty.getType());
    }

    @Test
    public void hasACategoryNameProperty() {
        DocumentationSchema category = modelMap.get("Category");
        assertTrue(category.getProperties().containsKey("name"));
    }

}
