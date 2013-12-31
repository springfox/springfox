package com.mangofactory.swagger.models;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.wordnik.swagger.core.DocumentationAllowableListValues;
import com.wordnik.swagger.core.DocumentationSchema;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.DocumentationSchemaMatchers.*;
import static com.mangofactory.swagger.models.ResolvedTypes.asResolvedType;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.*;

public class ArrayTest {
    private Map<String, DocumentationSchema> modelMap;

    enum TypeEnum {
        DOG,
        CAT
    }

    class ToTest {
        private Pet[] pets;
        private TypeEnum[] petTypeEnums;
        private Integer[] years;
        private String[] names;

        public Pet[] getPets() {
            return pets;
        }

        public void setPets(Pet[] pets) {
            this.pets = pets;
        }

        public TypeEnum[] getPetTypeEnums() {
            return petTypeEnums;
        }

        public void setPetTypeEnums(TypeEnum[] petTypeEnums) {
            this.petTypeEnums = petTypeEnums;
        }
        public Integer[] getYears() {
            return years;
        }

        public void setYears(Integer[] years) {
            this.years = years;
        }

        public String[] getNames() {
            return names;
        }

        public void setNames(String[] names) {
            this.names = names;
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
        assertThat(toTest.properties().size(), is(4));
        assertThat(toTest, hasProperty("pets", "Array"));
        DocumentationSchema petItems = toTest.properties().get("pets");
        assertThat(petItems.getItems().ref(), is("Pet"));

        assertThat(toTest, hasProperty("petTypeEnums", "Array"));
        DocumentationSchema petTypeEnumItems = toTest.properties().get("petTypeEnums");
        assertThat(petTypeEnumItems.getItems().ref(), is("TypeEnum"));

        assertThat(toTest, hasProperty("years", "Array"));
        DocumentationSchema yearItems = toTest.properties().get("years");
        assertThat(yearItems.getItems().ref(), is("int"));

        assertThat(toTest, hasProperty("names", "Array"));
        DocumentationSchema nameItems = toTest.properties().get("names");
        assertThat(nameItems.getItems().ref(), is("string"));
    }


}
