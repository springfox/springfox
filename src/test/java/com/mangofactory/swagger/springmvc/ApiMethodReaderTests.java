package com.mangofactory.swagger.springmvc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.List;

import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;

public class ApiMethodReaderTests {

	HandlerMethod handlerMethod;
	private ApiMethodReader methodReader;
	@Before @SneakyThrows
	public void setup()
	{
		SampleClass instance = new SampleClass();
		Method method = instance.getClass().getMethod("sampleMethod", String.class, String.class, String.class, String.class);
		handlerMethod = new HandlerMethod(instance, method);
		methodReader = new ApiMethodReader(handlerMethod);
	}
	@Test
	public void paramDataTypeDetectedCorrectly()
	{
		DocumentationOperation operation = methodReader.getOperation(RequestMethod.GET);
		List<DocumentationParameter> parameters = operation.getParameters();
		assertThat(parameters.get(0).dataType(),is(equalToIgnoringCase("string")));
		assertThat(parameters.get(1).dataType(),is(equalToIgnoringCase("string")));
	}
	
	@Test
	public void setsNickanameCorrectly()
	{
		DocumentationOperation operation = methodReader.getOperation(RequestMethod.GET);
		assertThat(operation.getNickname(),is(equalTo("sampleMethod")));
	}
	@Test
	public void apiParamNameTakesFirstPriority()
	{
		DocumentationOperation operation = methodReader.getOperation(RequestMethod.GET);
		List<DocumentationParameter> parameters = operation.getParameters();
		assertThat(parameters, hasSize(4));	
		assertThat(parameters.get(0).getName(), equalTo("documentationNameA"));
		assertThat(parameters.get(1).getName(), equalTo("mvcNameB"));
		assertThat(parameters.get(2).getName(), equalTo("modelAttributeC"));
		
		// Only available if debug data compiled in, so test excluded.
//		assertThat(parameters.get(3).getName(), equalTo("variableD"));
	}
	
	private final class SampleClass
	{
	@SuppressWarnings("unused")
	public void sampleMethod(
			@ApiParam(name="documentationNameA") @PathVariable("mvcNameA") String variableA,
			@PathVariable("mvcNameB") String variableB,
			@ModelAttribute("modelAttributeC") String variableC,
			String variableD
				)
		{
		}
		
	}
}
