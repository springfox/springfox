package com.mangofactory.swagger.models;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.wordnik.swagger.core.DocumentationSchema;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RecursiveTypeTest {
    private Map<String, DocumentationSchema> modelMap;

    class Pet {
        Pet parent;
        List<Pet> children;
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

        public Pet getParent() {
            return parent;
        }

        public void setParent(Pet parent) {
            this.parent = parent;
        }

        public List<Pet> getChildren() {
            return children;
        }

        public void setChildren(List<Pet> children) {
            this.children = children;
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
        DocumentationSchemaProvider provider = new DocumentationSchemaProvider(new TypeResolver(),
                new SwaggerConfiguration("1.1", "/"));
        modelMap = provider.getModelMap(new Model("Pet", asResolvedType(Pet.class)));
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
        assertEquals(5, pet.getProperties().size());
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
    public void schemaHasAParentProperty() {
        DocumentationSchema schema = modelMap.get("Pet");
        assertTrue(schema.getProperties().containsKey("parent"));
        DocumentationSchema property = schema.getProperties().get("parent");
        assertNotNull(property);
        assertEquals("Pet", property.getType());
    }

    @Test
    public void schemaHasChildrenProperty() {
        DocumentationSchema schema = modelMap.get("Pet");
        assertTrue(schema.getProperties().containsKey("children"));
        DocumentationSchema property = schema.getProperties().get("children");
        assertNotNull(property);
        assertEquals("List", property.getType());
        assertThat(property.getItems().ref(), is("Pet"));
    }

    @Test
    public void hasACategoryNameProperty() {
        DocumentationSchema category = modelMap.get("Category");
        assertTrue(category.getProperties().containsKey("name"));
    }

}
