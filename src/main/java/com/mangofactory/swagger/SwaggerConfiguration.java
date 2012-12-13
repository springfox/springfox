package com.mangofactory.swagger;

import lombok.Data;

import com.mangofactory.swagger.springmvc.MvcApiResource;
import com.wordnik.swagger.core.Documentation;

@Data
public class SwaggerConfiguration {

	private final String documentationBasePath;
	private final String swaggerVersion;
	private final String apiVersion;
	private final String basePath;

	public SwaggerConfiguration(String apiVersion, String swaggerVersion,
			String basePath, String documentationBasePath) {
		this.apiVersion = apiVersion;
		this.swaggerVersion = swaggerVersion;
		this.basePath = basePath;
		this.documentationBasePath = documentationBasePath;
	}

	public ControllerDocumentation newDocumentation(MvcApiResource resource) {
		return new ControllerDocumentation(apiVersion, swaggerVersion, basePath, resource);
	}
	public Documentation newDocumentation()
	{
		return new Documentation(apiVersion, swaggerVersion, basePath, null);
	}
}
