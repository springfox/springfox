package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.spring.DocumentationEndPoints.*;
import static com.mangofactory.swagger.spring.UriExtractor.*;

@Slf4j
public class DocumentationReader {

    private static final List<RequestMethod> allRequestMethods =
            Arrays.asList(RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT);
    private final SwaggerConfiguration configuration;
    private final Map<String, DocumentationEndPoint> endpointLookup = newHashMap();
    private final Map<String, ControllerDocumentation> resourceDocumentationLookup = newHashMap();
    private final EndpointReader endpointReader;
    private final OperationReader operationReader;
    @Getter
    private RequestMappingHandlerMapping handlerMapping;
    @Getter
    private Documentation documentation;

    public DocumentationReader(SwaggerConfiguration swaggerConfiguration, WebApplicationContext context,
                               RequestMappingHandlerMapping handlerMapping) {

        configuration = swaggerConfiguration;
        this.handlerMapping = handlerMapping;
        endpointReader = new EndpointReader(configuration);
        operationReader = new OperationReader(configuration);
        buildMappingDocuments(context);
    }

    private void buildMappingDocuments(WebApplicationContext context) {
        documentation = configuration.newDocumentation(context);
        processMethod(handlerMapping);
    }

    private ControllerDocumentation addChildDocumentIfMissing(ControllerAdapter resource,
                                                    ControllerDocumentation resourceDocumentation) {

        if (!resourceDocumentationLookup.containsKey(getDocumentationEndpointUri(resource.getControllerClass()))) {
            resourceDocumentationLookup.put(getDocumentationEndpointUri(resource.getControllerClass()),
                    resourceDocumentation);
        }
        return resourceDocumentationLookup.get(getDocumentationEndpointUri(resource.getControllerClass()));
    }

    private DocumentationEndPoint addEndpointDocumentationIfMissing(ControllerAdapter resource) {
        if (endpointLookup.containsKey(getDocumentationEndpointUri(resource.getControllerClass()))) {
            return endpointLookup.get(getDocumentationEndpointUri(resource.getControllerClass()));
        }

        DocumentationEndPoint endpoint = resource.describeAsDocumentationEndpoint();
        if (endpoint != null) {
            endpointLookup.put(getDocumentationEndpointUri(resource.getControllerClass()), endpoint);
            DocumentationReader.log.debug("Added resource listing: {}", resource.toString());
            documentation.addApi(endpoint);
        }
        return endpoint;
    }

    private void processMethod(RequestMappingHandlerMapping handlerMapping) {
        for (Entry<RequestMappingInfo, HandlerMethod> entry : handlerMapping.getHandlerMethods().entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            RequestMappingInfo mappingInfo = entry.getKey();

            ControllerAdapter resource = new ControllerAdapter(documentation, handlerMethod, configuration);

            // Don't document our own controllers
            if (resource.isInternalResource()
                    || configuration.isExcluded(getDocumentationEndpointUri(resource.getControllerClass()))) {
                continue;
            }

            DocumentationEndPoint endPoint = addEndpointDocumentationIfMissing(resource);
            ControllerDocumentation controllerDocumentation = addChildDocumentIfMissing(resource,
                    asDocumentation(documentation, endPoint, resource));

            for (String requestUri : mappingInfo.getPatternsCondition().getPatterns()) {
                DocumentationEndPoint childEndPoint = endpointReader.readEndpoint(handlerMethod, resource,
                        requestUri);
                controllerDocumentation.addEndpoint(childEndPoint);
                appendOperationsToEndpoint(controllerDocumentation, mappingInfo, handlerMethod, childEndPoint,
                        mappingInfo.getParamsCondition());
            }
        }
    }



    private void appendOperationsToEndpoint(ControllerDocumentation controllerDocumentation,
                                            RequestMappingInfo mappingInfo, HandlerMethod handlerMethod,
                                            DocumentationEndPoint endPoint, ParamsRequestCondition paramsCondition) {

        if (mappingInfo.getMethodsCondition().getMethods().isEmpty()) {
            // no methods have been specified, it means the endpoint is accessible for all methods
            appendOperationsToEndpoint(controllerDocumentation, handlerMethod, endPoint, allRequestMethods,
                    paramsCondition);
        } else {
            appendOperationsToEndpoint(controllerDocumentation, handlerMethod, endPoint,
                    mappingInfo.getMethodsCondition().getMethods(), paramsCondition);
        }
    }

    private void appendOperationsToEndpoint(ControllerDocumentation controllerDocumentation, HandlerMethod handlerMethod,
                                            DocumentationEndPoint endPoint,
                                            Collection<RequestMethod> methods, ParamsRequestCondition paramsCondition) {

        for (RequestMethod requestMethod : methods) {
            endPoint.addOperation(operationReader.readOperation(controllerDocumentation, handlerMethod,
                    paramsCondition, requestMethod));
        }
    }

    public ControllerDocumentation getDocumentation(String apiName) {
        for (ControllerDocumentation documentation : resourceDocumentationLookup.values()) {
            if (documentation.matchesName(apiName)) {
                return documentation;
            }
        }
        DocumentationReader.log.error("Could not find a matching resource for api with name '" + apiName + "'");
        return null;
    }
}