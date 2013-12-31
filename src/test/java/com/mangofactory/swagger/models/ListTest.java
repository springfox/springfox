package com.mangofactory.swagger.models;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.wordnik.swagger.core.DocumentationAllowableListValues;
import com.wordnik.swagger.core.DocumentationSchema;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.DocumentationSchemaMatchers.*;
import static com.mangofactory.swagger.models.ResolvedTypes.asResolvedType;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.*;

public class ListTest {
    private Map<String, DocumentationSchema> modelMap;

    enum TypeEnum {
        DOG,
        CAT
    }

    class ToTest {
        private List<Pet> pets;
        private List<TypeEnum> petTypeEnums;
        private List<Integer> years;
        private ArrayList<String> names;
        private List<Object> objects;

        ToTest(List<Pet> pets) {
            this.pets = pets;
        }

        public List<Pet> getPets() {
            return pets;
        }

        public void setPets(List<Pet> pets) {
            this.pets = pets;
        }

        public List<TypeEnum> getPetTypeEnums() {
            return petTypeEnums;
        }

        public void setPetTypeEnums(List<TypeEnum> petTypeEnums) {
            this.petTypeEnums = petTypeEnums;
        }

        public List<Integer> getYears() {
            return years;
        }

        public void setYears(List<Integer> years) {
            this.years = years;
        }

        public ArrayList<String> getNames() {
            return names;
        }

        public void setNames(ArrayList<String> names) {
            this.names = names;
        }

        public List<Object> getObjects() {
            return objects;
        }

        public void setObjects(List<Object> objects) {
            this.objects = objects;
        }
    }

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
        DocumentationSchemaProvider provider = new DocumentationSchemaProvider(new TypeResolver(),
                new SwaggerConfiguration("1.1", "/"));
        modelMap = provider.getModelMap(new Model("ToTest", asResolvedType(ToTest.class)));
    }

    @Test
    public void hasExpectedModels() {
        assertEquals(4, modelMap.size());
        assertThat(modelMap, hasKey("Category"));
        assertThat(modelMap, hasKey("Pet"));
        assertThat(modelMap, hasKey("TypeEnum"));
        assertThat(modelMap, hasKey("ToTest"));
    }

    @Test
    public void hasValidCategoryModel() {
        DocumentationSchema category = modelMap.get("Category");
        assertThat(category.properties().size(), is(1));
        assertThat(category, hasProperty("name", "string"));
    }

    @Test
    public void hasValidPetModel() {
        DocumentationSchema pet = modelMap.get("Pet");
        assertThat(pet.properties().size(), is(3));
        assertThat(pet, hasProperty("name", "string"));
        assertThat(pet, hasProperty("age", "int"));
        assertThat(pet, hasProperty("category", "Category"));
    }

    @Test
    public void hasValidTypeEnumModel() {
        DocumentationSchema typeEnum = modelMap.get("TypeEnum");
        assertThat(typeEnum.properties().size(), is(0));
        assertTrue(typeEnum.getAllowableValues() instanceof DocumentationAllowableListValues);
        List<String> values = ((DocumentationAllowableListValues) typeEnum.getAllowableValues()).getValues();
        assertEquals(2, values.size());
        assertThat(values, contains("DOG", "CAT"));
    }


    @Test
    public void hasValidToTestModel() {
        DocumentationSchema toTest = modelMap.get("ToTest");
        assertThat(toTest.properties().size(), is(5));
        assertThat(toTest, hasProperty("pets", "List"));
        DocumentationSchema petItems = toTest.properties().get("pets");
        assertThat(petItems.getItems().ref(), is("Pet"));

        assertThat(toTest, hasProperty("petTypeEnums", "List"));
        DocumentationSchema petTypeEnumItems = toTest.properties().get("petTypeEnums");
        assertThat(petTypeEnumItems.getItems().ref(), is("TypeEnum"));

        assertThat(toTest, hasProperty("years", "List"));
        DocumentationSchema yearItems = toTest.properties().get("years");
        assertThat(yearItems.getItems().ref(), is("int"));

        assertThat(toTest, hasProperty("names", "List"));
        DocumentationSchema nameItems = toTest.properties().get("names");
        assertThat(nameItems.getItems().ref(), is("string"));

        assertThat(toTest, hasProperty("objects", "List"));
        DocumentationSchema objects = toTest.properties().get("objects");
        assertNotNull(objects.getItems());
        assertThat(objects.getItems().ref(), is("any"));
    }


}
