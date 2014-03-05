package com.mangofactory.swagger.readers.operations.parameter;

import java.lang.annotation.Annotation;

import org.apache.commons.lang.NotImplementedException;

import com.wordnik.swagger.annotations.ApiParam;

/**
 * We can't mock mock @Interfaces using Mockito so just make a static class that does the job
 * this class well return required = true or false on demand
 *
 */
final class RequiredApiParam implements ApiParam {
	private final boolean required; 
	public RequiredApiParam(boolean required) {
		this.required = required;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		throw new NotImplementedException("Not Implemented");
	}

	@Override
	public String value() {
		throw new NotImplementedException("Not Implemented");
	}

	@Override
	public boolean required() {
		return required;
	}

	@Override
	public String name() {
		throw new NotImplementedException("Not Implemented");
	}

	@Override
	public String defaultValue() {
		throw new NotImplementedException("Not Implemented");
	}

	@Override
	public String allowableValues() {
		throw new NotImplementedException("Not Implemented");
	}

	@Override
	public boolean allowMultiple() {
		throw new NotImplementedException("Not Implemented");
	}

	@Override
	public String access() {
		throw new NotImplementedException("Not Implemented");
	}
}