package com.mangofactory.swagger.springmvc.controller;

import java.util.Collections;
import java.util.Comparator;

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
import com.wordnik.swagger.core.DocumentationEndPoint;

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
		Documentation resourceListing = apiReader.getResourceListing();
		Collections.sort(resourceListing.getApis(), new DocumentationEndPointPathComparator());
        return resourceListing;
	}
	
	@RequestMapping(value="/**",method=RequestMethod.GET, produces="application/json")
	public @ResponseBody ControllerDocumentation getApiDocumentation(HttpServletRequest request)
	{
	    String requestPath = (String) request.getAttribute( HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String apiName = requestPath.replace('/' + DocumentationController.CONTROLLER_ENDPOINT, "");
		ControllerDocumentation documentation = apiReader.getDocumentation(apiName);
		Collections.sort(documentation.getApis(), new DocumentationEndPointPathComparator());
        return documentation;
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
	
   protected final class DocumentationEndPointPathComparator implements Comparator<DocumentationEndPoint> {
        @Override
        public int compare(DocumentationEndPoint o1, DocumentationEndPoint o2) {
            return o1.getPath().compareTo(o2.getPath());
        }
    }
}
