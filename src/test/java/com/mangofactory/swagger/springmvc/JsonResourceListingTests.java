package com.mangofactory.swagger.springmvc;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import com.mangofactory.swagger.springmvc.test.TestConfiguration;

/**
 * Tests that exercise the documentation
 * API's and check the JSON returned is valid.
 * @author martypitt
 *
 */
public class JsonResourceListingTests {

	private MockMvc mockMvc;
	
	@Before
	public void setup()
	{
		mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
	}
	
	@Test @SneakyThrows
	public void testDocumentationEndpointServesOk()
	{
		mockMvc.perform(get("/api-docs"))
			.andExpect(status().isOk());
	}
	@Test @SneakyThrows
	public void testResourcePathNotListed()
	{
		mockMvc.perform(get("/api-docs"))
		.andExpect(jsonPath("$.resourcePath").doesNotExist());
	}
	@Test @SneakyThrows
	public void testApisContainCorrectApiList()
	{
		mockMvc.perform(get("/api-docs"))
			.andExpect(jsonPath("$.apiVersion").exists())
			.andExpect(jsonPath("$.apis").isArray());
	}
	@Test @SneakyThrows
	public void testApiPathIsRelativeToBasePath()
	{
		mockMvc.perform(get("/api-docs"))
		.andExpect(jsonPath("$.apiVersion").exists())
		.andExpect(jsonPath("$.apis[0].path").value(equalTo("/api-docs/pets")));
	}
	@Test @SneakyThrows
	public void testApiDescriptionIsCorrect()
	{
		mockMvc.perform(get("/api-docs"))
		.andExpect(jsonPath("$.apiVersion").exists())
		.andExpect(jsonPath("$.apis[0].description").value(equalTo("Operations about pets")));
	}
	@Test @SneakyThrows
	public void testApiVersionReturnedCorrectly()
	{
		mockMvc.perform(get("/api-docs"))
		.andExpect(jsonPath("$.apiVersion").value(equalTo("0.2")));
	}
	@Test @SneakyThrows
	public void testSwaggerVersionReturnedCorrectly()
	{
		mockMvc.perform(get("/api-docs"))
		.andExpect(jsonPath("$.swaggerVersion").value(equalTo("1.0")));
	}
	@Test @SneakyThrows
	public void testBasePathReturnedCorrectly()
	{
		mockMvc.perform(get("/api-docs"))
		.andExpect(jsonPath("$.basePath").value(equalTo("http://petstore.swagger.wordnik.com/api")));
	}
	
}
