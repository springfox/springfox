package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.spring.controller.DocumentationController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.AnnotatedElement;

/**
 * Generates a Resource listing for a given Api class.
 *
 * @author martypitt
 */
@Slf4j
public class ControllerAdapter {

    @Getter
    private final HandlerMethod handlerMethod;
    private final Class<?> controllerClass;
    private final SwaggerConfiguration configuration;
    private Documentation parent;

    public ControllerAdapter(Documentation parent, HandlerMethod handlerMethod, SwaggerConfiguration configuration) {
        this.parent = parent;
        this.handlerMethod = handlerMethod;
        this.configuration = configuration;
        // Workaround until SPR-9490 is fixed (see also issue #4 on github
        // Avoid NPE when handler.getBeanType() returns the CGLIB-generated class
        this.controllerClass = ClassUtils.getUserClass(this.handlerMethod.getBeanType());
    }

    public DocumentationEndPoint describeAsDocumentationEndpoint() {
        // This is the end-point for retrieving documentation about the api
        // Not the end point for the api itself
        String documentationUri = new UriBuilder(configuration.getDocumentationBasePath())
                .appendPath(getListingPath())
                .toString();
        return new DocumentationEndPoint(documentationUri, getApiDescription());
    }

    private String getListingPath() {
        Api apiAnnotation = controllerClass.getAnnotation(Api.class);
        if (apiAnnotation == null || apiAnnotation.listingPath().equals("")) {
            return getControllerUri();
        }
        return apiAnnotation.listingPath();
    }

    private String getApiDescription() {
        Api apiAnnotation = controllerClass.getAnnotation(Api.class);
        if (apiAnnotation == null) {
            return null;
        }
        return apiAnnotation.description();

    }

    public String getControllerUri() {
        String requestUri = resolveRequestUri(controllerClass);
        if (requestUri == null) {
            ControllerAdapter.log.info("Class {} has handler methods, but no class-level @RequestMapping. Continue with method-level {}",
                    controllerClass.getName(), handlerMethod.getMethod().getName());

            requestUri = resolveRequestUri(handlerMethod.getMethod());
            if (requestUri == null) {
                ControllerAdapter.log.warn("Unable to resolve the uri for class {} and method {}. No documentation will be generated",
                        controllerClass.getName(), handlerMethod.getMethod().getName());
                return null;
            }
        }
        return requestUri;
    }

    protected String resolveRequestUri(AnnotatedElement annotatedElement) {
        RequestMapping requestMapping = annotatedElement.getAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            ControllerAdapter.log.info("Class {} has no @RequestMapping", annotatedElement);
            return null;
        }
        String[] requestUris = requestMapping.value();
        if (requestUris == null || requestUris.length == 0) {
            ControllerAdapter.log.info("Class {} contains a @RequestMapping, but could not resolve the uri", annotatedElement);
            return null;
        }
        if (requestUris.length > 1) {
            ControllerAdapter.log.info("Class {} contains a @RequestMapping with multiple uri's. Only the first one will be documented.",
                    annotatedElement);
        }
        return requestUris[0];
    }

    @Override
    public String toString() {
        return "ApiResource for " + controllerClass.getSimpleName() + " at " + getControllerUri();
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public boolean isInternalResource() {
        return controllerClass == DocumentationController.class;
    }

    public Documentation documentation() {
        return parent;
    }
}
