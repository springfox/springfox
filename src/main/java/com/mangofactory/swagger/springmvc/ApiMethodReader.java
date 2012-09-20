package com.mangofactory.swagger.springmvc;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.core.DocumentationAllowableListValues;
import com.wordnik.swagger.core.DocumentationAllowableValues;
import com.wordnik.swagger.core.DocumentationError;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;

@Slf4j
public class ApiMethodReader {

	private final HandlerMethod handlerMethod;
	@Getter
	private String summary;
	@Getter
	private String notes;
	@Getter
	private Class<?> responseClass;
	@Getter
	private String tags;
	private String nickname;

	private boolean deprecated;
	@Getter
	private final List<DocumentationError> errors = Lists.newArrayList();
	private final List<DocumentationParameter> parameters = Lists.newArrayList();

	public ApiMethodReader(HandlerMethod handlerMethod) {
		this.handlerMethod = handlerMethod;
		documentOperation();
		documentParameters();
		documentExceptions();
	}

	private void documentOperation() {
		ApiOperation apiOperation = handlerMethod.getMethodAnnotation(ApiOperation.class);
		if (apiOperation != null)
		{
			summary = apiOperation.value();
			notes = apiOperation.notes();
			tags = apiOperation.tags();
		}
		nickname = handlerMethod.getMethod().getName();
		deprecated = handlerMethod.getMethodAnnotation(Deprecated.class) != null;
		responseClass = handlerMethod.getReturnType().getParameterType();
	}

	public DocumentationOperation getOperation(RequestMethod requestMethod) {
		DocumentationOperation operation = new DocumentationOperation(requestMethod.name(),summary,notes);
		operation.setDeprecated(deprecated);
		operation.setNickname(nickname);
		for (DocumentationParameter parameter : parameters)
			operation.addParameter(parameter);
		setTags(operation);
		operation.setResponseClass(responseClass.getSimpleName());
		
		for (DocumentationError error : errors)
			operation.addErrorResponse(error);
		return operation;
	}
	private void setTags(DocumentationOperation operation) {
		if (tags != null)
			operation.setTags(Arrays.asList(tags.split(",")));
	}

	private void documentParameters() {
		for (MethodParameter methodParameter : handlerMethod.getMethodParameters())
		{
			ApiParam apiParam = methodParameter.getParameterAnnotation(ApiParam.class);
			if (apiParam == null)
			{
				log.warn("{} is missing @ApiParam annotation - so generating default documentation", 
                         methodParameter.getMethod());
				generateDefaultParameterDocumentation(methodParameter);
				continue;
			}
			String name = selectBestParameterName(methodParameter);
			val allowableValues = convertToAllowableValues(apiParam.allowableValues());
			String description = apiParam.value();
			if (StringUtils.isEmpty(name))
				name = methodParameter.getParameterName();
			String paramType = getParameterType(methodParameter);
			String dataType = methodParameter.getParameterType().getSimpleName();
			RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
			boolean isRequired = apiParam.required();
			if (requestParam != null)
				isRequired = requestParam.required();
			DocumentationParameter documentationParameter = new DocumentationParameter(
					name, description, apiParam.internalDescription(),
					paramType, apiParam.defaultValue(), allowableValues,
					isRequired, apiParam.allowMultiple());
			documentationParameter.setDataType(dataType);
			parameters.add(documentationParameter);
		}
	}

	private String getParameterType(MethodParameter methodParameter) {
		RequestParam requestParam = methodParameter
				.getParameterAnnotation(RequestParam.class);
		if (requestParam != null)
			return "query";
		PathVariable pathVariable = methodParameter
				.getParameterAnnotation(PathVariable.class);
		if (pathVariable != null)
			return "path";
		RequestBody requestBody = methodParameter
				.getParameterAnnotation(RequestBody.class);
		if (requestBody != null)
			return "body";
		ModelAttribute modelAttribute = methodParameter
				.getParameterAnnotation(ModelAttribute.class);
		if (modelAttribute != null)
			return "body";
		return "query";
  }

  private String selectBestParameterName(MethodParameter methodParameter) {
		ApiParam apiParam = methodParameter.getParameterAnnotation(ApiParam.class);
		if (apiParam != null && !StringUtils.isEmpty(apiParam.name()))
			return apiParam.name();
		PathVariable pathVariable = methodParameter.getParameterAnnotation(PathVariable.class);
		if (pathVariable != null && !StringUtils.isEmpty(pathVariable.value()))
			return pathVariable.value();
		ModelAttribute modelAttribute = methodParameter.getParameterAnnotation(ModelAttribute.class);
		if (modelAttribute != null && !StringUtils.isEmpty(modelAttribute.value()))
			return modelAttribute.value();
		RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
		if (requestParam != null && !StringUtils.isEmpty(requestParam.value()))
			return requestParam.value();
		// Default
		return methodParameter.getParameterName();
		
	}

	private void generateDefaultParameterDocumentation(
			MethodParameter methodParameter) {
		String name = selectBestParameterName(methodParameter);
		String dataType = methodParameter.getParameterType().getSimpleName();
		String paramType = getParameterType(methodParameter);
		boolean isRequired = false;
		RequestParam requestParam = methodParameter
				.getParameterAnnotation(RequestParam.class);
		if (requestParam != null)
			isRequired = requestParam.required();
		DocumentationParameter documentationParameter = new DocumentationParameter(
				name, "", "", paramType, "", null, isRequired, false);
		documentationParameter.setDataType(dataType);
		parameters.add(documentationParameter);
	}

	protected DocumentationAllowableValues convertToAllowableValues(String csvString)
	{
		if (csvString.toLowerCase().startsWith("range[")) {
			val ranges = csvString.substring(6, csvString.length() - 1).split(",");
			return AllowableRangesParser.buildAllowableRangeValues(ranges, csvString);
		} else if (csvString.toLowerCase().startsWith("rangeexclusive[")) {
			val ranges = csvString.substring(15, csvString.length() - 1).split(",");
			return AllowableRangesParser.buildAllowableRangeValues(ranges, csvString);
		}
		// else..
		if (csvString == null || csvString.length() == 0)
			return null;
		val params = Arrays.asList(csvString.split(","));
		return new DocumentationAllowableListValues(params);
	}
	
	private void documentExceptions() {
		discoverSwaggerAnnotatedExceptions();
		discoverSpringMvcExceptions();
		discoverThrowsExceptions();
	}

	private void discoverThrowsExceptions() {
		Class<?>[] exceptionTypes = handlerMethod.getMethod().getExceptionTypes();
		for (Class<?> exceptionType : exceptionTypes)
		{
			appendErrorFromClass((Class<? extends Throwable>) exceptionType);
		}
	}

	private void discoverSpringMvcExceptions() {
		com.mangofactory.swagger.ApiErrors apiErrors = handlerMethod.getMethodAnnotation(com.mangofactory.swagger.ApiErrors.class);
		if (apiErrors == null)
			return;
		for (Class<? extends Throwable> exceptionClass : apiErrors.value())
		{
			appendErrorFromClass(exceptionClass);
		}
		
	}

	void appendErrorFromClass(Class<? extends Throwable> exceptionClass) {
		com.mangofactory.swagger.ApiError apiError = exceptionClass.getAnnotation(com.mangofactory.swagger.ApiError.class);
		if (apiError == null)
			return;
		errors.add(new DocumentationError(apiError.code(),apiError.reason()));
	}

	private void discoverSwaggerAnnotatedExceptions() {
		ApiErrors apiErrors = handlerMethod.getMethodAnnotation(ApiErrors.class);
		if (apiErrors == null)
			return;
		for (ApiError apiError : apiErrors.value())
		{
			errors.add(new DocumentationError(apiError.code(), apiError.reason()));
		}
	}
}
