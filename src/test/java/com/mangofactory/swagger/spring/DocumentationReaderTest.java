package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.spring.controller.DocumentationController;
import com.mangofactory.swagger.spring.test.TestConfiguration;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.test.context.WebContextLoader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = WebContextLoader.class,
        classes = TestConfiguration.class)
public class DocumentationReaderTest {

	@Autowired
	private DocumentationController controller;
    @Mock private HttpServletRequest request;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn("/pets");
    }

    @Test
	public void rootDocumentationEndpointPointsToApiDocs()
	{
		Documentation resourceListing = controller.getResourceListing();
		DocumentationEndPoint documentationEndPoint = resourceListing.getApis().get(0);
		assertThat(documentationEndPoint.getPath(),equalTo("/api-docs/pets"));
	}
	@Test
	public void findsDeclaredHandlerMethods()
	{
		Documentation resourceListing = controller.getResourceListing();
		assertThat(resourceListing.getApis().size(), equalTo(2));
		Documentation petsDocumentation = controller.getApiDocumentation(request);
		assertThat(petsDocumentation, is(notNullValue()));
		DocumentationEndPoint documentationEndPoint = resourceListing.getApis().get(0);
		assertEquals("/api-docs/pets" ,documentationEndPoint.getPath());
	}
	
	@Test
	public void findsExpectedMethods()
	{
		ControllerDocumentation petsDocumentation = controller.getApiDocumentation(request);
		DocumentationOperation operation = petsDocumentation.getEndPoint("/pets/{petId}",
                RequestMethod.GET).iterator().next();
		assertThat(operation, is(notNullValue()));
		assertThat(operation.getParameters().size(),equalTo(1));

        operation = petsDocumentation.getEndPoint("/pets/allMethodsAllowed", RequestMethod.GET).iterator().next();
		assertThat(operation, is(notNullValue()));
		operation = petsDocumentation.getEndPoint("/pets/allMethodsAllowed", RequestMethod.POST).iterator().next();
		assertThat(operation, is(notNullValue()));
		operation = petsDocumentation.getEndPoint("/pets/allMethodsAllowed", RequestMethod.DELETE).iterator().next();
		assertThat(operation, is(notNullValue()));
		operation = petsDocumentation.getEndPoint("/pets/allMethodsAllowed", RequestMethod.PUT).iterator().next();
		assertThat(operation, is(notNullValue()));
	}
}
