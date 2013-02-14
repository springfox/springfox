package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.spring.DocumentationEndPoints.*;

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
    private Map<String, HandlerMapping> handlerMappingBeans;
    @Getter
    private Documentation documentation;

    public DocumentationReader(WebApplicationContext context, SwaggerConfiguration swaggerConfiguration) {
        configuration = swaggerConfiguration;
        endpointReader = new EndpointReader(configuration);
        operationReader = new OperationReader(configuration);
        handlerMappingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
        buildMappingDocuments();
    }

    private void buildMappingDocuments() {
        documentation = configuration.newDocumentation();

        DocumentationReader.log.debug("Discovered {} candidates for documentation", handlerMappingBeans.size());
        for (HandlerMapping handlerMapping : handlerMappingBeans.values()) {
            if (RequestMappingHandlerMapping.class.isAssignableFrom(handlerMapping.getClass())) {
                processMethod((RequestMappingHandlerMapping) handlerMapping);
            } else {
                DocumentationReader.log.debug("Not documenting mapping of type {}, as it is not of a recognized type.", handlerMapping.getClass().getName());
            }
        }
    }

    private ControllerDocumentation addChildDocumentIfMissing(ControllerAdapter resource,
                                                    ControllerDocumentation resourceDocumentation) {

        if (!resourceDocumentationLookup.containsKey(resource.getControllerUri())) {
            resourceDocumentationLookup.put(resource.getControllerUri(), resourceDocumentation);
        }
        return resourceDocumentationLookup.get(resource.getControllerUri());
    }

    private DocumentationEndPoint addEndpointDocumentationIfMissing(ControllerAdapter resource) {
        if (endpointLookup.containsKey(resource.getControllerUri())) {
            return endpointLookup.get(resource.getControllerUri());
        }

        DocumentationEndPoint endpoint = resource.describeAsDocumentationEndpoint();
        if (endpoint != null) {
            endpointLookup.put(resource.getControllerUri(), endpoint);
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
            if (resource.isInternalResource()) {
                continue;
            }

            DocumentationEndPoint endPoint = addEndpointDocumentationIfMissing(resource);
            ControllerDocumentation controllerDocumentation = addChildDocumentIfMissing(resource,
                    asDocumentation(documentation, endPoint, resource));

            for (String requestUri : mappingInfo.getPatternsCondition().getPatterns()) {
                DocumentationEndPoint childEndPoint = endpointReader.readEndpoint(handlerMethod, resource, requestUri);
                controllerDocumentation.addEndpoint(childEndPoint);
                appendOperationsToEndpoint(controllerDocumentation, mappingInfo, handlerMethod, childEndPoint);
            }
        }
    }



    private void appendOperationsToEndpoint(ControllerDocumentation controllerDocumentation,
                                            RequestMappingInfo mappingInfo, HandlerMethod handlerMethod,
                                            DocumentationEndPoint endPoint) {

        if (mappingInfo.getMethodsCondition().getMethods().isEmpty()) {
            // no methods have been specified, it means the endpoint is accessible for all methods
            appendOperationsToEndpoint(controllerDocumentation, handlerMethod, endPoint, allRequestMethods);
        } else {
            appendOperationsToEndpoint(controllerDocumentation, handlerMethod, endPoint,
                    mappingInfo.getMethodsCondition().getMethods());
        }
    }

    private void appendOperationsToEndpoint(ControllerDocumentation controllerDocumentation, HandlerMethod handlerMethod,
                                            DocumentationEndPoint endPoint,
                                            Collection<RequestMethod> methods) {

        for (RequestMethod requestMethod : methods) {
            endPoint.addOperation(operationReader.readOperation(controllerDocumentation, handlerMethod, requestMethod));
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