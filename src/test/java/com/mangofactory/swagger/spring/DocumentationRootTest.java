package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.spring.controller.DocumentationController;
import com.mangofactory.swagger.spring.test.TestConfiguration;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests that exercise the documentation
 * API's and check the JSON returned is valid.
 *
 * @author martypitt
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = WebContextLoader.class, classes = TestConfiguration.class)
public class DocumentationRootTest {

    public static final String BUSINESS_ENTITY_SERVICES = "Business entity services";
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

//    @Test
//    @SneakyThrows
//    public void testApiPathIsRelativeToBasePath() {
//        mockMvc.perform(builder)
//                .andExpect(jsonPath("$.apiVersion").exists())
//                .andExpect(jsonPath("$.apis", hasSize(8)))
//                .andExpect(jsonPath("$.apis[0].path").value(equalTo("/api-docs/fancypets")))
//                .andExpect(jsonPath("$.apis[1].path").value(equalTo("/api-docs/features")))
//                .andExpect(jsonPath("$.apis[2].path").value(equalTo("/api-docs/petgrooming")))
//                .andExpect(jsonPath("$.apis[3].path").value(equalTo("/api-docs/pets/grooming")))
//                .andExpect(jsonPath("$.apis[4].path").value(equalTo("/api-docs/pets")))
//                .andExpect(jsonPath("$.api[5]").doesNotExist());
//    }

//    @Test
//    @SneakyThrows
//    public void testApiDescriptionIsCorrect() {
//        mockMvc.perform(builder)
//                .andExpect(jsonPath("$.apiVersion").exists())
//                .andExpect(jsonPath("$.apis", hasSize(8)))
//                .andExpect(jsonPath("$.apis[0].description").value(equalTo("Operations about fancy pets")))
//                .andExpect(jsonPath("$.apis[1].description").value(equalTo("Demonstration of features")))
//                .andExpect(jsonPath("$.apis[2].description").value(equalTo("Grooming operations for pets")))
//                .andExpect(jsonPath("$.apis[3].description").value(equalTo("Grooming operations for pets")))
//                .andExpect(jsonPath("$.apis[4].description").value(equalTo("Grooming operations for pets"))); //Need
//                // to fix this to be determinate
//    }

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

    @Test
    @SneakyThrows
    public void shouldHaveCorrectPathForBusinessServiceController() {
        mockMvc.perform(builder)
                .andExpect(jsonPath("$.apiVersion").exists())
                .andExpect(jsonPath("$.apis[0].path").value(equalTo("/api-docs/business-service")))
                .andExpect(jsonPath("$.apis[0].description").value(equalTo(BUSINESS_ENTITY_SERVICES)));
    }

    @Test
    public void shouldRespondWithDocumentOperationsWhenBusinessApiDocsCalled() throws Exception {
        mockMvc.perform(get("/api-docs/business-service")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(xpath("controllerDocumentation").exists())
                .andExpect(xpath("controllerDocumentation/apiVersion").string("2.0"))
                .andExpect(xpath("controllerDocumentation/apis[1]/description").string(equalTo(BUSINESS_ENTITY_SERVICES)))
                .andExpect(xpath("controllerDocumentation/apis[1]/operations[1]/nickname").string(equalTo("getAllBusinesses")))
                .andExpect(xpath("controllerDocumentation/apis[1]/operations[1]/httpMethod").string(equalTo("GET")))
                .andExpect(xpath("controllerDocumentation/apis[1]/operations[1]/summary").string(equalTo("Find a business by its id")))
                .andExpect(xpath("controllerDocumentation/apis[1]/path").string(equalTo("/businesses/{businessId}")));
    }
}
