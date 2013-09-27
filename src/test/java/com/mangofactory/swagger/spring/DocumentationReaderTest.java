package com.mangofactory.swagger.spring;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

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

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.spring.controller.DocumentationController;
import com.mangofactory.swagger.spring.test.TestConfiguration;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationOperation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = WebContextLoader.class, classes = TestConfiguration.class)
public class DocumentationReaderTest
{

	@Autowired
	private DocumentationController controller;
	@Mock
	private HttpServletRequest request;
	private Documentation resourceListing;
	private DocumentationEndPoint petsEndpoint;

	@Before
	public final void setup()
	{
		MockitoAnnotations.initMocks(this);
		when(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn("/pets");
		resourceListing = controller.getResourceListing();
		for ( DocumentationEndPoint endPoint : resourceListing.getApis() )
		{
			if ( "/api-docs/pets".equals(endPoint.getPath()) )
			{
				petsEndpoint = endPoint;
			}
		}

	}

	@Test
	public final void rootDocumentationEndpointPointsToApiDocs()
	{
		assertThat(petsEndpoint.getPath(), equalTo("/api-docs/pets"));
	}

	@Test
	public final void expectExcludedResourcesToBeExcluded()
	{
		for ( DocumentationEndPoint endPoint : resourceListing.getApis() )
		{
			if ( "/api-docs/excluded".equals(endPoint.getPath()) )
			{
				fail("Excluded resources should not be documented");
			}
		}
	}

	@Test
	public final void findsDeclaredHandlerMethods()
	{
		assertThat(resourceListing.getApis().size(), equalTo(2));
		assertEquals("/api-docs/pets", petsEndpoint.getPath());
		Documentation petsDocumentation = controller.getApiDocumentation(request);
		assertThat(petsDocumentation, is(notNullValue()));
	}

	@Test
	public final void findsExpectedMethods()
	{
		ControllerDocumentation petsDocumentation = controller.getApiDocumentation(request);
		DocumentationOperation operation = petsDocumentation.getEndPoint("/pets/{petId}", RequestMethod.GET).iterator()
				.next();
		assertThat(operation, is(notNullValue()));
		assertThat(operation.getParameters().size(), equalTo(1));

		operation = petsDocumentation.getEndPoint("/pets/allMethodsAllowed", RequestMethod.GET).iterator().next();
		assertThat(operation, is(notNullValue()));
		operation = petsDocumentation.getEndPoint("/pets/allMethodsAllowed", RequestMethod.POST).iterator().next();
		assertThat(operation, is(notNullValue()));
		operation = petsDocumentation.getEndPoint("/pets/allMethodsAllowed", RequestMethod.DELETE).iterator().next();
		assertThat(operation, is(notNullValue()));
		operation = petsDocumentation.getEndPoint("/pets/allMethodsAllowed", RequestMethod.PUT).iterator().next();
		assertThat(operation, is(notNullValue()));
	}

	@Test
	public final void testRegexStripping()
	{
		ControllerDocumentation petsDocs = controller.getApiDocumentation(request);
		DocumentationOperation op = petsDocs.getEndPoint("/pets/name/{petName}{ext}", RequestMethod.GET).iterator()
				.next();
		assertThat(op, is(notNullValue()));
		assertThat(op.getParameters().size(), equalTo(2));
		assertThat(op.getResponseClass(), equalTo("List[Pet]")); 

		op = petsDocs.getEndPoint("/pets/{id}/status{ext}", RequestMethod.GET).iterator().next();
		assertThat(op, is(notNullValue()));
		assertThat(op.getParameters().size(), equalTo(2));
		assertThat(op.getResponseClass(), op.getResponseClass(), is(String.class));

		// String uri = "/pets/name/{petName:[^\\.]*}{ext:\\.?[a-z0-9]*}";
		// String result = DocumentationReader.stripRequestMappingRegex(uri);
		// assertEquals(result, "/pets/name/{petName}{ext}");
		//
		// uri = "/pets/{petName:[^\\.]*}/livesAt{ext:\\.?[a-z0-9]*}";
		// result = DocumentationReader.stripRequestMappingRegex(uri);
		// assertEquals(result, "/pets/{petName}/livesAt{ext}");
	}

}
