package com.mangofactory.swagger.springmvc.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.test.context.WebContextLoader;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.springmvc.controller.DocumentationController;
import com.wordnik.swagger.core.Documentation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		loader=WebContextLoader.class,
		classes=TestConfiguration.class)
public class MvcApiReaderTests {

	@Autowired
	private DocumentationController controller;
	
	@Test
	public void findsDeclaredHandlerMethods()
	{
		Documentation resourceListing = controller.getResourceListing();
		assertThat(resourceListing.getApis(),hasSize(1));
		Documentation petsDocumentation = controller.getApiDocumentation("pets");
		assertThat(petsDocumentation, is(notNullValue()));
	}
	
	@Test
	public void findsExpectedMethods()
	{
		ControllerDocumentation petsDocumentation = controller.getApiDocumentation("pets");
	}
}
