package com.mangofactory.swagger.springmvc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.List;

import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

import com.mangofactory.swagger.ApiErrors;
import com.mangofactory.swagger.springmvc.test.Pet;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.core.DocumentationError;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;
import com.wordnik.swagger.sample.exception.BadRequestException;
import com.wordnik.swagger.sample.exception.NotFoundException;

public class ApiMethodReaderTest {

	HandlerMethod handlerMethod;
	private ApiMethodReader methodReader;
  private HandlerMethod handlerMethod2;
  private ApiMethodReader methodReader2;

  @Before @SneakyThrows
	public void setup()
	{
		SampleClass instance = new SampleClass();
		Method method = instance.getClass().getMethod("sampleMethod", String.class, String.class, String.class, String.class, String.class);
		handlerMethod = new HandlerMethod(instance, method);
		methodReader = new ApiMethodReader(handlerMethod);

		Method method2 = instance.getClass().getMethod("sampleMethod2", Pet.class);
		handlerMethod2 = new HandlerMethod(instance, method2);
    methodReader2 = new ApiMethodReader(handlerMethod2);
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
		assertThat(parameters, hasSize(5));	
		assertThat(parameters.get(0).getName(), equalTo("documentationNameA"));
		assertThat(parameters.get(1).getName(), equalTo("mvcNameB"));
		assertThat(parameters.get(2).getName(), equalTo("modelAttributeC"));
    assertThat(parameters.get(4).getName(), equalTo("requestParam1"));
		
		// Only available if debug data compiled in, so test excluded.
//		assertThat(parameters.get(3).getName(), equalTo("variableD"));
	}
	
	@Test
	public void detectsErrorsUsingSwaggerDeclaration()
	{
		methodReader = getExceptionMethod("exceptionMethodB");
		List<DocumentationError> errors = methodReader.getErrors();
		assertThat(errors, hasSize(2));
		DocumentationError error = errors.get(0);
		assertThat(error.code(), equalTo(302));
		assertThat(error.reason(), equalTo("Malformed request"));
	}
	@Test
	public void detectsErrorsUsingSpringMVCDeclaration()
	{
		methodReader = getExceptionMethod("exceptionMethodA");
		List<DocumentationError> errors = methodReader.getErrors();
		assertThat(errors, hasSize(2));
		DocumentationError error = errors.get(0);
		assertThat(error.code(), equalTo(404));
		assertThat(error.reason(), equalToIgnoringCase("Invalid ID supplied"));
	}
	@Test
	public void detectsErrorsUsingThrowsDeclaration()
	{
		methodReader = getExceptionMethod("exceptionMethodC");
		List<DocumentationError> errors = methodReader.getErrors();
		assertThat(errors, hasSize(1));
		DocumentationError error = errors.get(0);
		assertThat(error.code(), equalTo(404));
		assertThat(error.reason(), equalToIgnoringCase("Invalid ID supplied"));
	}
	@Test
  public void responseClass() {
    DocumentationOperation operation = methodReader
        .getOperation(RequestMethod.GET);
    assertThat(operation.getResponseClass(), equalToIgnoringCase("pet"));
  }
	@Test
  public void requestParamRequired() {
    DocumentationOperation operation = methodReader
        .getOperation(RequestMethod.GET);
    assertEquals(false,operation.getParameters().get(4).getRequired());
  }
	@Test
  public void paramType1() {
    DocumentationOperation operation = methodReader
        .getOperation(RequestMethod.GET);
    assertEquals("path",operation.getParameters().get(0).getParamType());
    assertEquals("path",operation.getParameters().get(1).getParamType());
    assertEquals("body",operation.getParameters().get(2).getParamType());
    assertEquals("query",operation.getParameters().get(4).getParamType());
  }
	@Test
  public void paramType() {
	  DocumentationOperation operation = methodReader2
        .getOperation(RequestMethod.POST);
    assertEquals("body",operation.getParameters().get(0).getParamType());
  }
	
	
	/// TEST SUPPORT
	@SneakyThrows
	private ApiMethodReader getExceptionMethod(String methodName) {
		SampleClass instance = new SampleClass();
		Method method = instance.getClass().getMethod(methodName);
		handlerMethod = new HandlerMethod(instance, method);
		methodReader = new ApiMethodReader(handlerMethod);
		return methodReader;
		
	}

	@SuppressWarnings("unused")
	private final class SampleClass
	{
	public @ResponseBody Pet sampleMethod(
			@ApiParam(name="documentationNameA") @PathVariable("mvcNameA") String variableA,
			@PathVariable("mvcNameB") String variableB,
			@ModelAttribute("modelAttributeC") String variableC,
			String variableD, @RequestParam(value="requestParam1", required=false) String variableE) {
	      return new Pet();
	}      

  public void sampleMethod2(@ApiParam(name = "pet") @RequestBody Pet pet) {
	}
	
	@ApiErrors({NotFoundException.class,BadRequestException.class})
	public void exceptionMethodA() {};
	
	@com.wordnik.swagger.annotations.ApiErrors({
			@com.wordnik.swagger.annotations.ApiError(code=302,reason="Malformed request"),
			@com.wordnik.swagger.annotations.ApiError(code=404,reason="Not found")}
			)
	public void exceptionMethodB() {};
	
	public void exceptionMethodC() throws NotFoundException {};
		
	}
}
