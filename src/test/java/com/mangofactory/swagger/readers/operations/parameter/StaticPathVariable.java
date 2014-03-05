package com.mangofactory.swagger.readers.operations.parameter;

import java.lang.annotation.Annotation;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * We can't use Mockito to mock out @Interfaces so just create a static class
 * that does the job
 */
final class StaticPathVariable implements PathVariable {
	@Override
	public Class<? extends Annotation> annotationType() {
		throw new NotImplementedException("Not Implemented");
	}

	@Override
	public String value() {
		throw new NotImplementedException("Not Implemented");
	}
}

