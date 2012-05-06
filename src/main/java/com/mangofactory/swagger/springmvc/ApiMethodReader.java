package com.mangofactory.swagger.springmvc;

import static org.springframework.test.web.server.result.MockMvcResultMatchers.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import com.google.common.collect.Lists;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.DocumentationAllowableListValues;
import com.wordnik.swagger.core.DocumentationAllowableValues;
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
	private final List<DocumentationParameter> parameters = Lists.newArrayList();

	public ApiMethodReader(HandlerMethod handlerMethod) {
		this.handlerMethod = handlerMethod;
		documentOperation();
		documentParameters();
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

	}

	public DocumentationOperation getOperation(RequestMethod requestMethod) {
		DocumentationOperation operation = new DocumentationOperation(requestMethod.name(),summary,notes);
		operation.setDeprecated(deprecated);
		operation.setNickname(nickname);
		for (DocumentationParameter parameter : parameters)
			operation.addParameter(parameter);
		setTags(operation);
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
				log.warn("{} is missing @ApiParam annotation - so generating default documentation");
				generateDefaultParameterDocumentation(methodParameter);
				continue;
			}
			String name = selectBestParameterName(methodParameter);
			val allowableValues = convertToAllowableValues(apiParam.allowableValues());
			String description = apiParam.value();
			if (StringUtils.isEmpty(name))
				name = methodParameter.getParameterName();
			String paramType = "path";
			String dataType = methodParameter.getParameterType().getSimpleName();
			DocumentationParameter documentationParameter = new DocumentationParameter(name, description, apiParam.internalDescription(),
								paramType,apiParam.defaultValue(), allowableValues,apiParam.required(),apiParam.allowMultiple());
			documentationParameter.setDataType(dataType);
			parameters.add(documentationParameter);

		}
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
		// Default
		return methodParameter.getParameterName();
		
	}

	private void generateDefaultParameterDocumentation(
			MethodParameter methodParameter) {
		String name = selectBestParameterName(methodParameter);
		String dataType = methodParameter.getParameterType().getSimpleName();
		String paramType = "path";
		DocumentationParameter documentationParameter = new DocumentationParameter(name, "", "",	paramType,"", null, true, false);
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
}
