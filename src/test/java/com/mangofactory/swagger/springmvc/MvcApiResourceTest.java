package com.mangofactory.swagger.springmvc;

import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class MvcApiResourceTest
{
	class SampleController
	{
		@RequestMapping( value = "/no-classlevel-requestmapping", method = RequestMethod.GET )
		public void sampleMethod()
		{

		}
	}

	@Test
	@SneakyThrows
	public void assertHandleNoClassLevelRequestMapping()
	{
		SampleController sampleController = new SampleController();
		Method sampleMethod = sampleController.getClass().getMethod("sampleMethod");
		HandlerMethod handlerMethod = new HandlerMethod(sampleController, sampleMethod);

		MvcApiResource mvcApiResource = new MvcApiResource(handlerMethod, null);
		String controllerUri = mvcApiResource.getControllerUri();

		assertThat(controllerUri, is(notNullValue()));
	}

}
