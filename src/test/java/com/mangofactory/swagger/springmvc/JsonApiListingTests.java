package com.mangofactory.swagger.springmvc;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import com.mangofactory.swagger.springmvc.test.TestConfiguration;

public class JsonApiListingTests {

private MockMvc mockMvc;
	
	@Before
	public void setup()
	{
		mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
	}
	
	@Test @SneakyThrows
	public void testDocumentationEndpointServesOk()
	{
		mockMvc.perform(get("/api-docs/pets"))
			.andExpect(status().isOk());
	}
	@Test @SneakyThrows
	public void getResult()
	{
		MvcResult result = mockMvc.perform(get("/api-docs/pets")).andReturn();
		String json = result.getResponse().getContentAsString();
		System.out.print(json);
	}
	
}
