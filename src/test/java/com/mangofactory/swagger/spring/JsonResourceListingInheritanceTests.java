package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.spring.controller.DocumentationController;
import com.mangofactory.swagger.spring.sample.configuration.ServicesTestConfiguration;
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

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Test that exercises the documentation
 * APIs for a class that inherits its {@link RequestMapping}s from an interface.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = WebContextLoader.class, classes = ServicesTestConfiguration.class)
public class JsonResourceListingInheritanceTests {

    @Autowired
    DocumentationController controller;
    private MockMvc mockMvc;
    private MockHttpServletRequestBuilder builder;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        builder = MockMvcRequestBuilders.get("/api-docs").accept(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testInheritedService() throws Exception {
        mockMvc.perform(builder)
                .andExpect(jsonPath("$.apis[0].path").value(equalTo("/api-docs/business-service")));
    }

}
