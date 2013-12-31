package com.mangofactory.swagger.models;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.spring.filters.OperationFilterTest;
import com.wordnik.swagger.core.DocumentationSchema;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Map;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.AlternateTypeProcessingRule.alternate;
import static com.mangofactory.swagger.models.ResolvedTypes.*;
import static org.junit.Assert.*;

public class NestedAlternateTypeTest {
    private Map<String, DocumentationSchema> modelMap;

    class TypeWithNestedAlternate {
       URI alternateType;

        public URI getAlternateType() {
            return alternateType;
        }

        public void setAlternateType(URI alternateType) {
            this.alternateType = alternateType;
        }
    }


    @Before
    public void setup() {
        modelMap = newHashMap();
        SwaggerConfiguration configuration = new SwaggerConfiguration("1.1", "/");
        TypeResolver resolver = new TypeResolver();
        configuration.getTypeProcessingRules().add(alternate(resolver.resolve(URI.class),
                resolver.resolve(OperationFilterTest.MyUrl.class)));
        DocumentationSchemaProvider provider = new DocumentationSchemaProvider(new TypeResolver(),
                configuration);
        modelMap = provider.getModelMap(new Model("nestedAlternate", asResolvedType(TypeWithNestedAlternate.class)));
    }

    @Test
    public void model_is_populated_correctly() {
        assertTrue(modelMap.containsKey("TypeWithNestedAlternate"));
        DocumentationSchema typeWithNestedAlt = modelMap.get("TypeWithNestedAlternate");
        assertNotNull(typeWithNestedAlt.getProperties());
        assertEquals(1, typeWithNestedAlt.getProperties().size());
        assertTrue(modelMap.containsKey("MyUrl"));
    }

}
