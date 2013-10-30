package com.mangofactory.swagger.spring;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.test.context.WebContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mangofactory.swagger.spring.controller.DocumentationController;
import com.mangofactory.swagger.spring.test.TestConfiguration;
/**
 * Test that exercises the documentation
 * APIs for a class that inherits its {@link RequestMapping}s from an interface.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = WebContextLoader.class, classes = TestConfiguration.class)
public class JsonResourceListingInheritanceTest {

    @Autowired
    DocumentationController controller;
    private MockMvc mockMvc;
    private MockHttpServletRequestBuilder builder;
    private MockHttpServletRequestBuilder operationBuilder;
    private MockHttpServletRequestBuilder alternativeChildOperationBuilder;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        builder = MockMvcRequestBuilders.get("/api-docs").accept(MediaType.APPLICATION_JSON);
        operationBuilder = MockMvcRequestBuilders.get("/api-docs/child").accept(MediaType.APPLICATION_JSON);
        alternativeChildOperationBuilder = MockMvcRequestBuilders.get("/api-docs/alternativeChild").accept(MediaType.APPLICATION_JSON);
    }

    @Test
    @SneakyThrows
    public void testServiceLevel() {
        mockMvc.perform(builder)
                .andExpect(jsonPath("$.apis[*].path", hasItem("/api-docs/child")))
                .andExpect(jsonPath("$.apis[*].description", hasItem("Inherited via Interface Services")))
                .andExpect(jsonPath("$.apis[*].path", hasItem("/api-docs/alternativeChild")));
    }

    @Test
    public void testOperationLevel() throws Exception {
        mockMvc.perform(operationBuilder)
                .andExpect(jsonPath("$.apis[0].path", equalTo("/child/child-method")))
                .andExpect(jsonPath("$.apis[0].description", equalTo("Inherited via Interface Services")))
                .andExpect(jsonPath("$.apis[0].operations[0].summary", equalTo("Go ahead and get something, while taking in a parameter")))
                .andExpect(jsonPath("$.apis[0].operations[0].notes", equalTo("This operation is cool")));
    }

    @Test
    public void testParameterLevel() throws Exception {
        mockMvc.perform(operationBuilder)
                .andExpect(jsonPath("$.apis[0].operations[0].parameters[0].name", equalTo("parameter")))
                .andExpect(jsonPath("$.apis[0].operations[0].parameters[0].description", equalTo("The parameter to do stuff with")))
                .andExpect(jsonPath("$.apis[0].operations[0].parameters[0].notes", equalTo("The Coolest Parameter")));
    }

    @Test
    public void testAlternativeListingOperationLevel() throws Exception {
        mockMvc.perform(alternativeChildOperationBuilder)
                .andExpect(jsonPath("$.apis[0].path", equalTo("/anotherChild/alternative-child-method")))
                .andExpect(jsonPath("$.apis[0].description", equalTo("ALTERNATIVE - Inherited via Interface Services")))
                .andExpect(jsonPath("$.apis[0].operations[0].summary", equalTo("ALTERNATIVE - Go ahead and get something, while taking in a parameter")))
                .andExpect(jsonPath("$.apis[0].operations[0].notes", equalTo("ALTERNATIVE - This operation is cool")));
    }

}
