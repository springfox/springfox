package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.spring.controller.DocumentationController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;

import static com.mangofactory.swagger.spring.UriExtractor.*;

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
        return new DocumentationEndPoint(documentationUri, getApiDescription(controllerClass));
    }

    private String getListingPath() {
        Api apiAnnotation = controllerClass.getAnnotation(Api.class);
        if (apiAnnotation == null || apiAnnotation.listingPath().equals("")) {
            return getClassLevelUri(controllerClass);
        }
        return apiAnnotation.listingPath();
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
        return String.format("ApiResource for %s at %s", controllerClass.getSimpleName(),
                getMethodLevelUri(controllerClass, handlerMethod));
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
