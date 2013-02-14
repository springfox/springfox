package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.spring.controller.DocumentationController;
import com.mangofactory.swagger.spring.test.TestConfiguration;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.test.context.WebContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests that exercise the documentation
 * API's and check the JSON returned is valid.
 *
 * @author martypitt
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = WebContextLoader.class, classes = TestConfiguration.class)
public class JsonResourceListingTests {

    @Autowired
    DocumentationController controller;
    private MockMvc mockMvc;
    private MockHttpServletRequestBuilder builder;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        builder = MockMvcRequestBuilders.get("/api-docs");
    }

    @Test
    @SneakyThrows
    public void testDocumentationEndpointServesOk() {
        mockMvc.perform(builder)
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void testResourcePathNotListed() {
        mockMvc.perform(builder)
                .andExpect(jsonPath("$.resourcePath").doesNotExist());
    }

    @Test
    @SneakyThrows
    public void testApisContainCorrectApiList() {
        mockMvc.perform(builder)
                .andExpect(jsonPath("$.apiVersion").exists())
                .andExpect(jsonPath("$.apis").isArray());
    }

    @Test
    @SneakyThrows
    public void testApiPathIsRelativeToBasePath() {
        mockMvc.perform(builder)
                .andExpect(jsonPath("$.apiVersion").exists())
                .andExpect(jsonPath("$.apis[0].path").value(equalTo("/api-docs/pets")));
    }

    @Test
    @SneakyThrows
    public void testApiDescriptionIsCorrect() {
        mockMvc.perform(builder)
                .andExpect(jsonPath("$.apiVersion").exists())
                .andExpect(jsonPath("$.apis[0].description").value(equalTo("Operations about pets")));
    }

    @Test
    @SneakyThrows
    public void testApiVersionReturnedCorrectly() {
        mockMvc.perform(builder)
                .andExpect(jsonPath("$.apiVersion").value(equalTo("2.0")));
    }

    @Test
    @SneakyThrows
    public void testSwaggerVersionReturnedCorrectly() {
        mockMvc.perform(builder)
                .andExpect(jsonPath("$.swaggerVersion").value(equalTo("1.0")));
    }

    @Test
    @SneakyThrows
    public void testBasePathReturnedCorrectly() {
        mockMvc.perform(builder)
                .andExpect(jsonPath("$.basePath").value(equalTo("/some-path")));
    }

}
