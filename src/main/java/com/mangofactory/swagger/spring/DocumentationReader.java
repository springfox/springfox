package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.spring.DocumentationEndPoints.*;

public class DocumentationReader {
    private static final Logger LOG = LogManager.getLogger(DocumentationReader.class);

    private static final List<RequestMethod> allRequestMethods =
            Arrays.asList(RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT);
    private final SwaggerConfiguration configuration;
    private final Map<String, DocumentationEndPoint> endpointByControllerLookup = newHashMap();
    private final Map<String, DocumentationEndPoint> endpointLookup = newHashMap();
    private final Map<String, ControllerDocumentation> resourceDocumentationLookup = newHashMap();
    private final EndpointReader endpointReader;
    private final OperationReader operationReader;
    private final WebApplicationContext context;
    private boolean isMappingBuilt = false;

    private List<RequestMappingHandlerMapping> handlerMappings;

    private Documentation documentation;
    public DocumentationReader(SwaggerConfiguration swaggerConfiguration, WebApplicationContext context,
                               List<RequestMappingHandlerMapping> handlerMappings) {

        configuration = swaggerConfiguration;
        this.context = context;
        this.handlerMappings = handlerMappings;
        endpointReader = new EndpointReader(configuration);
        operationReader = new OperationReader(configuration);
    }

    public List<RequestMappingHandlerMapping> getHandlerMappings() {
        return handlerMappings;
    }

    private synchronized void buildMappingDocuments(WebApplicationContext context) {
        if (!isMappingBuilt) {
            documentation = configuration.newDocumentation(context);
            for (RequestMappingHandlerMapping handlerMapping : handlerMappings) {
                processMethod(handlerMapping);
            }
            isMappingBuilt = true;
        }
    }

    private ControllerDocumentation addChildDocumentIfMissing(ControllerDocumentation resourceDocumentation) {
        if (!resourceDocumentationLookup.containsKey(resourceDocumentation.getResourcePath())) {
            resourceDocumentationLookup.put(resourceDocumentation.getResourcePath(),
                    resourceDocumentation);
        }
        return resourceDocumentationLookup.get(resourceDocumentation.getResourcePath());
    }

    private List<DocumentationEndPoint> addEndpointDocumentationsIfMissing(ControllerAdapter resource) {
        List<DocumentationEndPoint> endpoints = newArrayList();
        for (String uri: resource.getControllerUris()) {
            final String key = String.format("%s-%s", resource.getControllerClass().getSimpleName(), uri);
            if (endpointByControllerLookup.containsKey(key)) {
                endpoints.add(endpointByControllerLookup.get(key));
            }
        }
        if (!endpoints.isEmpty()) {
            return endpoints;
        }

        for (DocumentationEndPoint endpoint : endpoints = resource.describeAsDocumentationEndpoints()) {
            final String key = String.format("%s-%s", resource.getControllerClass().getSimpleName(), endpoint.path());
            if (!endpointByControllerLookup.containsKey(key)) {
                endpointByControllerLookup.put(key, endpoint);
            }
        }
        for (DocumentationEndPoint endpoint: endpoints) {
            if (!endpointLookup.containsKey(toApiUri(endpoint.getPath()))) {
                endpointLookup.put(toApiUri(endpoint.getPath()), endpoint);
                documentation.addApi(endpoint);
            }
        }
        return endpoints;
    }

    private String toApiUri(String path) {
        return path.substring(configuration.getDocumentationBasePath().length());
    }

    private void processMethod(RequestMappingHandlerMapping handlerMapping) {
        for (Entry<RequestMappingInfo, HandlerMethod> entry : handlerMapping.getHandlerMethods().entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            RequestMappingInfo mappingInfo = entry.getKey();

            ControllerAdapter resource = new ControllerAdapter(documentation, handlerMethod, configuration);

            // Don't document our own controllers
            if (resource.shouldSkipDocumentation()) {
                continue;
            }

            for(DocumentationEndPoint endPoint: addEndpointDocumentationsIfMissing(resource)) {
                ControllerDocumentation controllerDocumentation = addChildDocumentIfMissing(
                        asDocumentation(documentation, toApiUri(endPoint.path()), configuration.getSchemaProvider()));

                for (String requestUri : mappingInfo.getPatternsCondition().getPatterns()) {
                    DocumentationEndPoint childEndPoint = endpointReader.readEndpoint(handlerMethod, resource,
                            requestUri);
                     String resourcePath = controllerDocumentation.getResourcePath();
                  if (requestUri.contains(resourcePath)
                            || resourcePathMatchesController(resourcePath, resource)) {
                        controllerDocumentation.addEndpoint(childEndPoint);
                        appendOperationsToEndpoint(controllerDocumentation, mappingInfo, handlerMethod, childEndPoint,
                                mappingInfo.getParamsCondition());
                    }
                }
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

    private void appendOperationsToEndpoint(ControllerDocumentation controllerDocumentation,
            HandlerMethod handlerMethod, DocumentationEndPoint endPoint, Collection<RequestMethod> methods,
            ParamsRequestCondition paramsCondition) {

        for (RequestMethod requestMethod : methods) {
            endPoint.addOperation(operationReader.readOperation(controllerDocumentation, handlerMethod,
                    paramsCondition, requestMethod));
        }
    }

    public ControllerDocumentation getDocumentation(String apiName) {
        ensureDocumentationReady();
        for (ControllerDocumentation documentation : resourceDocumentationLookup.values()) {
            if (documentation.matchesName(apiName)) {
                return documentation;
            }
        }
        LOG.error("Could not find a matching resource for api with name '" + apiName + "'");
        return null;
    }

    public Documentation getDocumentation() {
        ensureDocumentationReady();
        return documentation;
    }

    private synchronized void ensureDocumentationReady() {
        if (!isMappingBuilt) {
            buildMappingDocuments(context);
            isMappingBuilt = true;
        }
    }

    private boolean resourcePathMatchesController(String resourcePath, ControllerAdapter controllerAdapter) {
        String simpleName = controllerAdapter.getControllerClass().getSimpleName();
        String controllerDescription = Descriptions.splitCamelCase(simpleName, "-").toLowerCase();
        return resourcePath.endsWith(controllerDescription);
    }
}