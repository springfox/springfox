package com.mangofactory.swagger.springmvc.controller;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.springmvc.MvcApiReader;
import com.wordnik.swagger.core.Documentation;

@Controller
@RequestMapping("/apidoc")
public class DocumentationController implements InitializingBean {

	private static final String CONTROLLER_ENDPOINT = "apidoc";
	
	@Getter @Setter
	private String apiVersion = "1.0";
	@Getter @Setter
	private String swaggerVersion = "1.0";
	
	@Getter @Setter
	private String basePath = "/";
	
	@Autowired
	private WebApplicationContext wac;
	
	@Getter
	private MvcApiReader apiReader;
	
	@RequestMapping(method=RequestMethod.GET, produces="application/json")
	public @ResponseBody Documentation getResourceListing()
	{
		return apiReader.getResourceListing();
	}
	
	@RequestMapping(value="/{apiName}",method=RequestMethod.GET, produces="application/json")
	public @ResponseBody ControllerDocumentation getApiDocumentation(@PathVariable("apiName") String apiName)
	{
		return apiReader.getDocumentation(apiName);
	}

	// TODO : 
	// Initializing apiReader here so that consumers only have
	// to declare a single bean, rather than many.
	// A better approach would be to use a custom xml declaration
	// and parser - like <swagger:documentation ... />
	public void afterPropertiesSet() throws Exception {
		String documentationBasePath = basePath;
		if (!basePath.endsWith("/"))
			documentationBasePath += "/";
		documentationBasePath += CONTROLLER_ENDPOINT;
		
		SwaggerConfiguration config = new SwaggerConfiguration(apiVersion,swaggerVersion,documentationBasePath);
		apiReader = new MvcApiReader(wac, config);
	}
	
}
