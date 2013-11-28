package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.spring.controller.DocumentationController;
import com.mangofactory.swagger.spring.sample.configuration.ServicesTestConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.test.context.WebContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = WebContextLoader.class, classes = ServicesTestConfiguration.class)
public class PetServiceTest {
    MockMvc mockMvc;
    private MockHttpServletRequestBuilder builder;
    @Autowired
    private DocumentationController documentationController;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(documentationController).build();
        builder = MockMvcRequestBuilders.get("/api-docs/pets");
    }

    @Test
    public void testDocumentationEndpointServesOk() throws Exception {
        mockMvc.perform(builder)
                .andExpect(status().isOk());
    }

    @Test
    public void getResult() throws Exception {
        MvcResult result = mockMvc.perform(builder).andReturn();
        String json = result.getResponse().getContentAsString();
        System.out.print(json);
    }

}
