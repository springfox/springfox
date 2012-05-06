package com.mangofactory.swagger;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMethod;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.Maps;
import com.mangofactory.swagger.springmvc.MvcApiResource;
import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationOperation;

@Slf4j
public class ControllerDocumentation extends Documentation {

	private final MvcApiResource resource;
	
	private final Map<String, com.wordnik.swagger.core.DocumentationEndPoint> endpointMap = Maps.newHashMap(); 

	public ControllerDocumentation(String apiVersion, String swaggerVersion,
			String basePath, MvcApiResource resource) {
		super(apiVersion,swaggerVersion,basePath,resource.getControllerUri());
		this.resource = resource;
	}
	private Class<?> getControllerClass()
	{
		return resource.getControllerClass();
	}
	/**
	 * Returns a {@link DocumentationEndPoint} for the provided method - creating
	 * one if needed.
	 * @return
	 */
	public DocumentationEndPoint getEndPoint(String requestUri) {
		if (!endpointMap.containsKey(requestUri))
		{
			// TODO : The intent of description is confusing - looking at the Pets example,
			// it seems to only be used when describing a Resource (ie., an API as an endpoint),
			// rather than the endpoints of the API themselves.
			DocumentationEndPoint endPoint = new DocumentationEndPoint(requestUri, getApiDescription());
			endpointMap.put(requestUri, endPoint);
			addApi(endPoint);
			log.debug("Added documentation endpoint for class {} at endpoint {}",getControllerClass().getName(),requestUri);
		}
		
		return endpointMap.get(requestUri);
	}

	private String getApiDescription() {
		Api apiAnnotation = getControllerClass().getAnnotation(Api.class);
		return (apiAnnotation != null) ? apiAnnotation.description() : "";
	}
	public Boolean matchesName(String name) {
		String nameWithForwardSlash = (name.startsWith("/")) ? name : "/" + name;
		String nameWithoutForwardSlash = (name.startsWith("/")) ? name.substring(1) : name;
		
		return getResourcePath().equals(nameWithoutForwardSlash) || 
				getResourcePath().equals(nameWithForwardSlash); 
	}
	public DocumentationOperation getEndPoint(String requestUri, RequestMethod method) {
		DocumentationEndPoint endPoint = getEndPoint(requestUri);
		if (endPoint == null || endPoint.getOperations() == null)
			return null;
		for (DocumentationOperation operation : endPoint.getOperations())
		{
			if (operation.getHttpMethod().equals(method.name()))
				return operation;
		}
		return null;
	}
}
