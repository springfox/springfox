package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.wordnik.swagger.core.DocumentationSchema;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.*;
import static org.junit.Assert.*;

public class GenericTypeTest {
    private Map<String, DocumentationSchema> modelMap;

    class Pet {
        String name;
        int age;

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
    }

    @Before
    public void setup() {
        modelMap = newHashMap();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixInAnnotations(HttpHeaders.class, HttpHeadersMixin.class);
        DocumentationSchemaProvider provider = new DocumentationSchemaProvider(new TypeResolver(),
                new SwaggerConfiguration("1.1", "/"), new Jackson2SchemaDescriptor(new SwaggerConfiguration("1.1",
                "/"), objectMapper));
        modelMap = provider.getModelMap(new Model("pet", asResolvedType()));
    }

    private class HttpHeadersMixin {
        @JsonIgnore
        public void setIfNoneMatch(List<String> ifNoneMatchList) {
        }

        @JsonIgnore
        public void setLocation(URI uri) {
        }
    }


    private ResolvedType asResolvedType() {
        TypeResolver resolver = new TypeResolver();
        return resolver.resolve(ResponseEntity.class, Pet.class);
    }

    @Test
    public void hasExpectedModels() {
        assertEquals(8, modelMap.size());
    }

    @Test
    public void hasAPetModel() {
        assertTrue(modelMap.containsKey("Pet"));
        DocumentationSchema pet = modelMap.get("Pet");
        assertNotNull(pet.getProperties());
        assertEquals(2, pet.getProperties().size());
    }

    @Test
    public void hasAResponseEntityModel() {
        assertTrue(modelMap.containsKey("ResponseEntity«Pet»"));
        DocumentationSchema responseEntity = modelMap.get("ResponseEntity«Pet»");
        assertNotNull(responseEntity.getProperties());
        assertEquals(3, responseEntity.getProperties().size());
    }


}
