package com.mangofactory.swagger.springmvc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.test.context.WebContextLoader;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.springmvc.controller.DocumentationController;
import com.mangofactory.swagger.springmvc.test.TestConfiguration;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		loader=WebContextLoader.class,
		classes=TestConfiguration.class)
public class MvcApiReaderTest {

	@Autowired
	private DocumentationController controller;

	@Test
	public void givenADocumentedApi_that_thePathReferencesTheDocumentationEndPoint()
	{
		Documentation resourceListing = controller.getResourceListing();
		DocumentationEndPoint documentationEndPoint = resourceListing.getApis().get(0);
		assertThat(documentationEndPoint.getPath(),equalTo("resources/pets"));
	}
	@Test
	public void findsDeclaredHandlerMethods()
	{
		Documentation resourceListing = controller.getResourceListing();
		assertThat(resourceListing.getApis().size(),equalTo(1));
		Documentation petsDocumentation = controller.getApiDocumentation("pets");
		assertThat(petsDocumentation, is(notNullValue()));
		DocumentationEndPoint documentationEndPoint = resourceListing.getApis().get(0);
		assertEquals("/pets" ,documentationEndPoint.getPath());
	}
	
	@Test
	public void findsExpectedMethods()
	{
		ControllerDocumentation petsDocumentation = controller.getApiDocumentation("pets");
		DocumentationOperation operation = petsDocumentation.getEndPoint("/pets/{petId}",RequestMethod.GET);
		assertThat(operation, is(notNullValue()));
		assertThat(operation.getParameters().size(),equalTo(1));
		DocumentationParameter parameter = operation.getParameters().get(0);

		operation = petsDocumentation.getEndPoint("/pets/allMethodsAllowed", RequestMethod.GET);
		assertThat(operation, is(notNullValue()));
		operation = petsDocumentation.getEndPoint("/pets/allMethodsAllowed", RequestMethod.POST);
		assertThat(operation, is(notNullValue()));
		operation = petsDocumentation.getEndPoint("/pets/allMethodsAllowed", RequestMethod.DELETE);
		assertThat(operation, is(notNullValue()));
		operation = petsDocumentation.getEndPoint("/pets/allMethodsAllowed", RequestMethod.PUT);
		assertThat(operation, is(notNullValue()));
	}
}
