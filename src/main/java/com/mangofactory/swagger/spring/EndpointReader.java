package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.filters.Filters;
import com.wordnik.swagger.core.DocumentationEndPoint;
import org.springframework.web.method.HandlerMethod;

public class EndpointReader {
    private final SwaggerConfiguration configuration;

    public EndpointReader(SwaggerConfiguration configuration) {
        this.configuration = configuration;
    }

    DocumentationEndPoint readEndpoint(HandlerMethod handlerMethod, ControllerAdapter resource, String requestUri) {
        DocumentationEndPoint childEndPoint = new DocumentationEndPoint(requestUri, "");
        FilterContext<DocumentationEndPoint> filterContext
                = new FilterContext<DocumentationEndPoint>(childEndPoint);
        filterContext.put("swagger", configuration);
        filterContext.put("controllerClass", resource.getControllerClass());
        filterContext.put("handlerMethod", handlerMethod);
        Filters.Fn.applyFilters(configuration.getEndpointFilters(), filterContext);
        return childEndPoint;
    }
}