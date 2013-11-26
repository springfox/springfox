package com.mangofactory.swagger.spring;

import com.google.common.base.Joiner;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.annotations.ApiIgnore;
import com.mangofactory.swagger.annotations.ApiInclude;
import com.mangofactory.swagger.spring.controller.DocumentationController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;

import java.util.List;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.spring.UriExtractor.*;

/**
 * Generates a Resource listing for a given Api class.
 *
 * @author martypitt
 */
public class ControllerAdapter {

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

    public HandlerMethod getHandlerMethod() {
        return handlerMethod;
    }

    public List<DocumentationEndPoint> describeAsDocumentationEndpoints() {
        List<DocumentationEndPoint> endpoints = newArrayList();
        for(String listingPath: getListingPaths()) {
            // This is the end-point for retrieving documentation about the api
            // Not the end point for the api itself
            String documentationUri = new UriBuilder(configuration.getDocumentationBasePath())
                    .appendPath(listingPath)
                    .toString();
            endpoints.add(new DocumentationEndPoint(documentationUri, getApiDescription(controllerClass)));
        }
        return endpoints;
    }

    private List<String> getListingPaths() {
        Api apiAnnotation = controllerClass.getAnnotation(Api.class);
        if (apiAnnotation == null || apiAnnotation.listingPath().equals("")) {
            return controllerUris(controllerClass);
        }
        return newArrayList(apiAnnotation.listingPath());
    }

    private String getApiDescription(Class<?> controllerClass) {
        Api apiAnnotation = controllerClass.getAnnotation(Api.class);
        if (apiAnnotation == null) {
            return null;
        }
        return apiAnnotation.description();

    }

    @Override
    public String toString() {
        return String.format("ApiResource for %s at [%s]", controllerClass.getSimpleName(),
                Joiner.on(",").skipNulls().join(controllerUris(controllerClass)));
    }

    public List<String> getControllerUris() {
        return controllerUris(controllerClass);
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

    public boolean isIgnored() {
        ApiIgnore annotation = handlerMethod.getMethodAnnotation(ApiIgnore.class);
        return annotation != null;
    }

    public boolean hasIncludeOverride() {
        ApiInclude annotation = handlerMethod.getMethodAnnotation(ApiInclude.class);
        return annotation != null;
    }

    public boolean shouldSkipDocumentation() {
        return isInternalResource()
                || excludedControllerIsNotExplicitlyIncluded()
                || isIgnored();
    }

    private boolean excludedControllerIsNotExplicitlyIncluded() {
        return configuration.isExcluded(controllerUris(controllerClass))
                        && !hasIncludeOverride();
    }
}
