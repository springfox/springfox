package com.mangofactory.swagger.springmvc.controller;

import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerMapping;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.springmvc.MvcApiReader;
import com.wordnik.swagger.core.Documentation;

@Controller
@RequestMapping('/' + DocumentationController.CONTROLLER_ENDPOINT)
public class DocumentationController implements InitializingBean {

	public static final String CONTROLLER_ENDPOINT = "api-docs";
	
	@Getter @Setter
	private String apiVersion = "1.0";
	@Getter @Setter
	private String swaggerVersion = "1.1";
	
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
	
	@RequestMapping(value="/**",method=RequestMethod.GET, produces="application/json")
	public @ResponseBody ControllerDocumentation getApiDocumentation(HttpServletRequest request)
	{
	    String requestPath = (String) request.getAttribute( HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String apiName = requestPath.replace('/' + DocumentationController.CONTROLLER_ENDPOINT, "");
		return apiReader.getDocumentation(apiName);
	}

	// TODO : 
	// Initializing apiReader here so that consumers only have
	// to declare a single bean, rather than many.
	// A better approach would be to use a custom xml declaration
	// and parser - like <swagger:documentation ... />
	public void afterPropertiesSet() throws Exception {
		String documentationBasePath = "/" + CONTROLLER_ENDPOINT;
		SwaggerConfiguration config = new SwaggerConfiguration(apiVersion,swaggerVersion,basePath,documentationBasePath);
		apiReader = new MvcApiReader(wac, config);
	}
	
}
