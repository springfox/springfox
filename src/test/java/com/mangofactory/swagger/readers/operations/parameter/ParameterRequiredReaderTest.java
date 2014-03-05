package com.mangofactory.swagger.readers.operations.parameter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.MethodParameter;

import com.mangofactory.swagger.readers.operation.parameter.ParameterRequiredReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;

public class ParameterRequiredReaderTest {

	// Class under test
	private static final ParameterRequiredReader READER = new ParameterRequiredReader();

	// Mocks
	private static final RequestMappingContext CONTEXT = mock(RequestMappingContext.class);
	private static final MethodParameter methodParameter = mock(MethodParameter.class);

	// Static data
	private static final Annotation pathParameter = new StaticPathVariable();
	private static final Annotation apiNotRequiredParameter = new RequiredApiParam(false);
	private static final Annotation apiRequiredParameter = new RequiredApiParam(true);

	@Before
	public void setup() {
		reset(CONTEXT);
		reset(methodParameter);
		when(CONTEXT.get("methodParameter")).thenReturn(methodParameter);
	}
	
	@Test
	public void testSingleRequiredAnnotation() {
		// A parameter with one required annotation should then be required
		when(methodParameter.getParameterAnnotations()).thenReturn(new Annotation[] {apiRequiredParameter});
		READER.execute(CONTEXT);
		verify(CONTEXT).put("required", true);
	}
	
	@Test
	public void testSingleNotRequiredAnnotation() {
		// A parameter with one not-required annotation should then be not required
		when(methodParameter.getParameterAnnotations()).thenReturn(new Annotation[] {apiNotRequiredParameter});
		READER.execute(CONTEXT);
		verify(CONTEXT).put("required", false);
	}
	
	@Test
	public void testPathParmeter() {
		// path parameters are always required
		when(methodParameter.getParameterAnnotations()).thenReturn(new Annotation[] {pathParameter});
		READER.execute(CONTEXT);
		verify(CONTEXT).put("required", true);
	}
	
	@Test
	public void testMultipleAnnotationsIncludingPathParam_pathParameterFirst() {
		// path parameters are always required
		when(methodParameter.getParameterAnnotations()).thenReturn(new Annotation[] {pathParameter, apiNotRequiredParameter});
		READER.execute(CONTEXT);
		verify(CONTEXT).put("required", true);
	}

	@Test
	public void testMultipleAnnotationsIncludingPathParam_pathParameterLast() {
		// path parameters are always required
		when(methodParameter.getParameterAnnotations()).thenReturn(new Annotation[] {apiNotRequiredParameter, pathParameter});
		READER.execute(CONTEXT);
		verify(CONTEXT).put("required", true);
	}
	
	@Test
	public void testMultipleAnnotations() {
		// a parameter with one required annotation and one not-required annotation should be required 
		when(methodParameter.getParameterAnnotations()).thenReturn(new Annotation[] {apiNotRequiredParameter, apiRequiredParameter});
		READER.execute(CONTEXT);
		verify(CONTEXT).put("required", true);
	}

}
