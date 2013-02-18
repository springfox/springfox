package com.mangofactory.swagger.spring;

import com.wordnik.swagger.core.Documentation;
import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ControllerAdapterTest
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

		ControllerAdapter controllerAdapter = new ControllerAdapter(new Documentation(), handlerMethod, null);
		String controllerUri = controllerAdapter.getControllerUri();

		assertThat(controllerUri, is(notNullValue()));
	}

}
