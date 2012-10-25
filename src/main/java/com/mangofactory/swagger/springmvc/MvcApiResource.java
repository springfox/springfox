package com.mangofactory.swagger.springmvc;

import java.lang.reflect.AnnotatedElement;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.springmvc.controller.DocumentationController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.core.DocumentationEndPoint;

/**
 * Generates a Resource listing for a given Api class.
 * @author martypitt
 *
 */
@Slf4j
public class MvcApiResource {

	@Getter
	private final HandlerMethod handlerMethod;
	private final Class<?> controllerClass;
	private final SwaggerConfiguration configuration;

	public MvcApiResource(HandlerMethod handlerMethod, SwaggerConfiguration configuration) {
		this.handlerMethod = handlerMethod;
		this.configuration = configuration;
		// Workaround until SPR-9490 is fixed (see also issue #4 on github
		// Avoid NPE when handler.getBeanType() returns the CGLIB-generated class
		this.controllerClass = ClassUtils.getUserClass(handlerMethod.getBeanType());
	}

	public DocumentationEndPoint describeAsEndpoint()
	{
		DocumentationEndPoint endPoint = new DocumentationEndPoint(getListingPath(),getApiDescription());
		return endPoint;
	}
	
	private String getListingPath() {
		Api apiAnnotation = controllerClass.getAnnotation(Api.class);
		if (apiAnnotation == null || apiAnnotation.listingPath().equals(""))
			return getControllerUri();
		return apiAnnotation.listingPath();
	}

    public ControllerDocumentation createEmptyApiDocumentation()
	{
		String resourcePath = getControllerUri();
		if (resourcePath == null)
			return null;
		
		return configuration.newDocumentation(this);
	}
	
	private String getApiDescription()
	{
		Api apiAnnotation = controllerClass.getAnnotation(Api.class);
		if (apiAnnotation == null)
			return null;
		return apiAnnotation.description();
		
	}
    
	public String getControllerUri() 
	{ 
		String requestUri = resolveRequestUri(controllerClass);
		if (requestUri == null)
		{
			log.info("Class {} has handler methods, but no class-level @RequestMapping. Continue with method-level {}",
							 controllerClass.getName(), handlerMethod.getMethod().getName());

			requestUri = resolveRequestUri(handlerMethod.getMethod());
			if (requestUri == null)
			{
				log.warn("Unable to resolve the uri for class {} and method {}. No documentation will be generated",
								 controllerClass.getName(), handlerMethod.getMethod().getName());
				return null;
			}
		}
		return requestUri;
	}
    
	protected String resolveRequestUri(AnnotatedElement annotatedElement)
	{
		RequestMapping requestMapping = annotatedElement.getAnnotation( RequestMapping.class );
		if (requestMapping == null)
		{
			log.info("Class {} has no @RequestMapping", annotatedElement);
			return null;
		}
		String[] requestUris = requestMapping.value();
		if (requestUris == null || requestUris.length == 0)
		{
			log.info("Class {} contains a @RequestMapping, but could not resolve the uri", annotatedElement);
			return null;
		}
		if (requestUris.length > 1)
		{
			log.info("Class {} contains a @RequestMapping with multiple uri's. Only the first one will be documented.",
							 annotatedElement);
		}
		return requestUris[0];
	}
	
	@Override
	public String toString()
	{
		return "ApiResource for " + controllerClass.getSimpleName() + " at " + getControllerUri();
	}

	public Class<?> getControllerClass() {
		return controllerClass;
	}

	public boolean isInternalResource() {
		return controllerClass == DocumentationController.class;
	}

	
}
