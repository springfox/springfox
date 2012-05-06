package com.mangofactory.swagger.springmvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.MethodParameter;
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
public class MethodApiReader {

	private final HandlerMethod handlerMethod;
	@Getter
	private String summary;
	@Getter
	private String notes;
	@Getter
	private Class<?> responseClass;
	@Getter
	private String tags;

	private boolean deprecated;
	private final List<DocumentationParameter> parameters = Lists.newArrayList();

	public MethodApiReader(HandlerMethod handlerMethod) {
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
		deprecated = handlerMethod.getMethodAnnotation(Deprecated.class) != null;

	}

	@SuppressWarnings("unchecked")
	public DocumentationOperation getOperation(RequestMethod requestMethod) {
		DocumentationOperation operation = new DocumentationOperation(requestMethod.name(),summary,notes);
		operation.setDeprecated(deprecated);
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
			ApiParam apiParam = methodParameter.getMethodAnnotation(ApiParam.class);
			if (apiParam == null)
			{
				log.warn("{} is missing @ApiParam annotation - so generating default documentation");
				generateDefaultParameterDocumentation(methodParameter);
				continue;
			}
			String name = apiParam.name();
			val allowableValues = convertToAllowableValues(apiParam.allowableValues());
			String description = apiParam.value();
			if (StringUtils.isEmpty(name))
				name = methodParameter.getParameterName();
			String paramType = methodParameter.getParameterType().getSimpleName();
			parameters.add(new DocumentationParameter(name, description, apiParam.internalDescription(),
								paramType,apiParam.defaultValue(), allowableValues,apiParam.required(),apiParam.allowMultiple()));

		}
	}

	private void generateDefaultParameterDocumentation(
			MethodParameter methodParameter) {
		String name = methodParameter.getParameterName();
		String paramType = methodParameter.getParameterType().getSimpleName();
		parameters.add(new DocumentationParameter(name, "", "",	paramType,"", null, true, false));
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

	/*
	 * protected def convertToAllowableValues(csvString: String, paramType: String = null): DocumentationAllowableValues = {
    if (csvString.toLowerCase.startsWith("range[")) {
      val ranges = csvString.substring(6, csvString.length() - 1).split(",")
      return buildAllowableRangeValues(ranges, csvString)
    } else if (csvString.toLowerCase.startsWith("rangeexclusive[")) {
      val ranges = csvString.substring(15, csvString.length() - 1).split(",")
      return buildAllowableRangeValues(ranges, csvString)
    } else {
      if (csvString == null || csvString.length == 0) {
        null
      } else {
        val params = csvString.split(",").toList
        paramType match {
          case null => new DocumentationAllowableListValues(params)
          case "string" => new DocumentationAllowableListValues(params)
        }
      }
    }
	 */


}
