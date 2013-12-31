package com.mangofactory.swagger.models;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.wordnik.swagger.core.DocumentationAllowableListValues;
import com.wordnik.swagger.core.DocumentationSchema;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.ResolvedTypes.asResolvedType;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class EnumTest {
    private Map<String, DocumentationSchema> modelMap;

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


    @Before
    public void setup() {
        modelMap = newHashMap();
        DocumentationSchemaProvider provider = new DocumentationSchemaProvider(new TypeResolver(),
                new SwaggerConfiguration("1.1", "/"));
        modelMap = provider.getModelMap(new Model("ExampleWithEnums", asResolvedType(ExampleWithEnums.class)));
    }


    @Test
    public void hasExpectedModels() {
        assertEquals(2, modelMap.size());
    }

    @Test
    public void hasAnExampleEnumModel() {
        assertTrue(modelMap.containsKey("ExampleEnum"));
        DocumentationSchema exampleEnum = modelMap.get("ExampleEnum");
        assertThat(exampleEnum.getProperties().size(), is(0));
        assertTrue(exampleEnum.getAllowableValues() instanceof DocumentationAllowableListValues);
        assertEquals(2, ((DocumentationAllowableListValues) exampleEnum.getAllowableValues()).getValues().size());
    }

    @Test
    public void hasAnExampleModel() {
        assertTrue(modelMap.containsKey("ExampleWithEnums"));
        DocumentationSchema exampleWithEnums = modelMap.get("ExampleWithEnums");
        assertNotNull(exampleWithEnums.getProperties());
        assertEquals(1, exampleWithEnums.getProperties().size());
    }

    @Test
    public void exampleWithEnumsHasAnEnumProperty() {
        DocumentationSchema schema = modelMap.get("ExampleWithEnums");
        assertTrue(schema.getProperties().containsKey("exampleEnum"));
        DocumentationSchema anEnum = schema.getProperties().get("exampleEnum");
        assertNotNull(anEnum);
        assertEquals("ExampleEnum", anEnum.getType());
    }

}
