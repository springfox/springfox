package com.mangofactory.swagger;

import com.wordnik.swagger.core.DocumentationSchema;
import org.junit.Before;
import org.junit.Test;

import static com.mangofactory.swagger.Models.Fn.*;
import static org.junit.Assert.*;

public class ModelsTest {
    private Model exampleWithEnums;
    private Model exampleWithoutEnums;

    enum ExampleEnum {
        ONE,
        TWO
    }
    class ExampleWithEnums {
        private ExampleEnum exampleEnum;

        public ExampleEnum getExampleEnum() {
            return exampleEnum;
        }

        public void setExampleEnum(ExampleEnum exampleEnum) {
            this.exampleEnum = exampleEnum;
        }
    }

    class ExampleWithoutEnums {
        private int someNum;
        private ExampleWithEnums example;

        public ExampleWithEnums getExample() {
            return example;
        }

        public void setExample(ExampleWithEnums example) {
            this.example = example;
        }

        public int getSomeNum() {
            return someNum;
        }

        public void setSomeNum(int someNum) {
            this.someNum = someNum;
        }
    }

    @Before
    public void setup() {
        exampleWithEnums = new Model("test", ExampleWithEnums.class);
        exampleWithoutEnums = new Model("test", ExampleWithoutEnums.class);
    }

    @Test
    public void enumsAreRenderedAsString() {
        DocumentationSchema sut = modelToSchema().apply(exampleWithEnums);
        assertNotNull(sut);
        assertNotNull(sut.getProperties());
        assertEquals(1, sut.getProperties().size());
        DocumentationSchema exampleEnum = sut.getProperties().get("exampleEnum");
        assertEquals("string", exampleEnum.getType());
    }


    @Test
    public void nestedTypesContainEnumProperties() {
        DocumentationSchema sut = modelToSchema().apply(exampleWithoutEnums);
        assertNotNull(sut);
        assertNotNull(sut.getProperties());
        assertEquals(2, sut.getProperties().size());
        DocumentationSchema someNum = sut.getProperties().get("someNum");
        assertEquals("integer", someNum.getType());
        DocumentationSchema example = sut.getProperties().get("example");
        assertEquals("object", example.getType());
        assertNotNull(example.getProperties());
        assertEquals(1, example.getProperties().size());
        DocumentationSchema exampleEnum = example.getProperties().get("exampleEnum");
        assertEquals("string", exampleEnum.getType());
    }
}
