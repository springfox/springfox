package com.mangofactory.swagger.springmvc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.springmvc.controller.DocumentationController;
import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.DocumentationEndPoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * Generates a Resource listing for a given Api class.
 * 
 * @author martypitt
 * 
 */
@Slf4j
public class MvcApiResource {

	@Getter
	private final HandlerMethod handlerMethod;
	private final Class<?> controllerClass;
	private final SwaggerConfiguration configuration;

	public MvcApiResource(HandlerMethod handlerMethod,
			SwaggerConfiguration configuration) {
		this.handlerMethod = handlerMethod;
		this.configuration = configuration;
		// Workaround until SPR-9490 is fixed (see also issue #4 on github
		// Avoid NPE when handler.getBeanType() returns the CGLIB-generated
		// class
		this.controllerClass = ClassUtils.getUserClass(handlerMethod
				.getBeanType());
	}

	public DocumentationEndPoint describeAsEndpoint() {
		DocumentationEndPoint endPoint = new DocumentationEndPoint(
				getControllerUri(), getApiDescription());
		return endPoint;
	}

	public ControllerDocumentation createEmptyApiDocumentation() {
		String resourcePath = getControllerUri();
		if (resourcePath == null)
			return null;

		return configuration.newDocumentation(this);
	}

	private String getApiDescription() {
		Api apiAnnotation = getDeclaredAnnotation(controllerClass, Api.class);
		if (apiAnnotation == null)
			return null;
		return apiAnnotation.description();

	}

	@SuppressWarnings("unchecked")
	private <A extends Annotation> A getDeclaredAnnotation(
			Class<?> controllerClass, Class<A> annotation) {
		Annotation[] declaredAnnotations = controllerClass
				.getDeclaredAnnotations();
		for (Annotation thisAnnotation : declaredAnnotations) {
			if (thisAnnotation.equals(annotation)) {
				return (A) thisAnnotation;
			}
		}
		return null;
	}

	public String getControllerUri() {
		String requestUri = resolveRequestUri(controllerClass);
		if (requestUri == null) {
			 log.info("Class {} has handler methods, but no class-level @RequestMapping. Continue with method-level {}",
			 controllerClass.getName(), handlerMethod.getMethod().getName());

			requestUri = resolveRequestUri(handlerMethod.getMethod());
			if (requestUri == null) {
				 log.warn("Unable to resolve the uri for class {} and method {}. No documentation will be generated",
				 controllerClass.getName(),
				 handlerMethod.getMethod().getName());
				return null;
			}
		}
		return requestUri;
	}

	private String resolveRequestUri(Method method) {
		RequestMapping requestMapping = method
				.getAnnotation(RequestMapping.class);
		if (requestMapping == null) {
			requestMapping = getRequestMappingsFromClassInterfaces(method);

		}
		return getRequestUri(requestMapping, method);
	}

	protected String resolveRequestUri(Class<?> controllerClass) {
		RequestMapping requestMapping = controllerClass
				.getAnnotation(RequestMapping.class);
		if (requestMapping == null) {
			requestMapping = getRequestMappingsFromClassInterfaces(controllerClass);
		}
		return getRequestUri(requestMapping, controllerClass);
	}

	private RequestMapping getRequestMappingsFromClassInterfaces(Method method) {
		Class<?>[] interfaces = method.getDeclaringClass().getInterfaces();
		if (ArrayUtils.isEmpty(interfaces)) {
			return null;
		}
		for (Class<?> thisInterface : interfaces) {
			Method[] methods = thisInterface.getMethods();
			if (ArrayUtils.isEmpty(methods)) {
				continue;
			}
			for (Method thisMethod : methods) {
				if (methodsEquals(method, thisMethod)) {
					return thisMethod.getAnnotation(RequestMapping.class);
				}
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private boolean methodsEquals(Method method, Method thisMethod) {
		if (!method.getName().equals(thisMethod.getName())
				|| !method.getReturnType().equals(thisMethod.getReturnType())) {
			return false;
		}
		Class[] params1 = method.getParameterTypes();
		Class[] params2 = thisMethod.getParameterTypes();
		if (params1.length != params2.length) {
			return false;
		}
		for (int i = 0; i < params1.length; i++) {
			if (params1[i] != params2[i])
				return false;
		}
		return true;
	}

	private String getRequestUri(RequestMapping requestMapping, AnnotatedElement annotatedElement) {
		if (requestMapping == null) {
			 log.info("Class {} has no @RequestMapping", annotatedElement);
			return null;
		}
		String[] requestUris = requestMapping.value();
		if (requestUris == null || requestUris.length == 0) {
			 log.info("Class {} contains a @RequestMapping, but could not resolve the uri",
			 annotatedElement);
			return null;
		}
		if (requestUris.length > 1) {
			 log.info("Class {} contains a @RequestMapping with multiple uri's. Only the first one will be documented.",
			 annotatedElement);
		}
		return requestUris[0];
	}

	private RequestMapping getRequestMappingsFromClassInterfaces(
			Class<?> controllerClass) {
		Class<?>[] interfaces = controllerClass.getInterfaces();
		if (ArrayUtils.isEmpty(interfaces)) {
			return null;
		}
		for (Class<?> thisInterface : interfaces) {
			final RequestMapping requestMapping = thisInterface.getAnnotation(RequestMapping.class);
			if (requestMapping != null) {
				return requestMapping;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "ApiResource for " + controllerClass.getSimpleName() + " at "
				+ getControllerUri();
	}

	public Class<?> getControllerClass() {
		return controllerClass;
	}

	public boolean isInternalResource() {
		return controllerClass == DocumentationController.class;
	}

}
