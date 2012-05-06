package com.mangofactory.swagger;

import lombok.Data;

import com.mangofactory.swagger.springmvc.MvcApiResource;
import com.wordnik.swagger.core.Documentation;

@Data
public class SwaggerConfiguration {

	public SwaggerConfiguration(String apiVersion, String swaggerVersion,
			String basePath) {
		this.apiVersion = apiVersion;
		this.swaggerVersion = swaggerVersion;
		this.basePath = basePath;
	}
	private String swaggerVersion;
	private String apiVersion;
	private String basePath;

	public ControllerDocumentation newDocumentation(MvcApiResource resource) {
		return new ControllerDocumentation(apiVersion, swaggerVersion, basePath, resource);
	}
	public Documentation newDocumentation()
	{
		return new Documentation(apiVersion, swaggerVersion, basePath, null);
	}
}
