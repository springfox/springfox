package com.mangofactory.swagger.spring;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.mangofactory.swagger.spring.DocumentationEndPoints.asDocumentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;

@Slf4j
public class DocumentationReader {

    private static final Pattern requestMappingURIRegex = Pattern.compile("\\{([^}]*)\\}");
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
    @Getter
    private List<RequestMappingHandlerMapping> handlerMappings;
    private Documentation documentation;

    public DocumentationReader(final SwaggerConfiguration swaggerConfiguration, final WebApplicationContext context,
                               final List<RequestMappingHandlerMapping> handlerMappings) {

        configuration = swaggerConfiguration;
        this.context = context;
        this.handlerMappings = handlerMappings;
        endpointReader = new EndpointReader(configuration);
        operationReader = new OperationReader(configuration);
    }

    private void buildMappingDocuments(final WebApplicationContext context) {
        documentation = configuration.newDocumentation(context);
        for (RequestMappingHandlerMapping handlerMapping : handlerMappings) {
            processMethod(handlerMapping);
        }
    }

    private ControllerDocumentation addChildDocumentIfMissing(final ControllerDocumentation resourceDocumentation) {
        if (!resourceDocumentationLookup.containsKey(resourceDocumentation.getResourcePath())) {
            resourceDocumentationLookup.put(resourceDocumentation.getResourcePath(),
                    resourceDocumentation);
        }
        return resourceDocumentationLookup.get(resourceDocumentation.getResourcePath());
    }

    private List<DocumentationEndPoint> addEndpointDocumentationsIfMissing(final ControllerAdapter resource) {
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

    private String toApiUri(final String path) {
        return path.substring(configuration.getDocumentationBasePath().length());
    }

    private void processMethod(final RequestMappingHandlerMapping handlerMapping) {
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
                    requestUri = stripRequestMappingRegex(requestUri);
                    DocumentationEndPoint childEndPoint = endpointReader.readEndpoint(handlerMethod, resource,
                            requestUri);
                    if (requestUri.contains(controllerDocumentation.getResourcePath())) {
                        controllerDocumentation.addEndpoint(childEndPoint);
                        appendOperationsToEndpoint(controllerDocumentation, mappingInfo, handlerMethod, childEndPoint,
                                mappingInfo.getParamsCondition());
                    }
                }
            }
        }
    }

    private void appendOperationsToEndpoint(final ControllerDocumentation controllerDocumentation,
                              final RequestMappingInfo mappingInfo, final HandlerMethod handlerMethod,
                              final DocumentationEndPoint endPoint, final ParamsRequestCondition paramsCondition) {

        if (mappingInfo.getMethodsCondition().getMethods().isEmpty()) {
            // no methods have been specified, it means the endpoint is accessible for all methods
            appendOperationsToEndpoint(controllerDocumentation, handlerMethod, endPoint, allRequestMethods,
                    paramsCondition);
        } else {
            appendOperationsToEndpoint(controllerDocumentation, handlerMethod, endPoint,
                    mappingInfo.getMethodsCondition().getMethods(), paramsCondition);
        }
    }

    private void appendOperationsToEndpoint(final ControllerDocumentation controllerDocumentation,
            final HandlerMethod handlerMethod, final DocumentationEndPoint endPoint, 
            final Collection<RequestMethod> methods,
            final ParamsRequestCondition paramsCondition) {

        for (RequestMethod requestMethod : methods) {
            endPoint.addOperation(operationReader.readOperation(controllerDocumentation, handlerMethod,
                    paramsCondition, requestMethod));
        }
    }

    public final ControllerDocumentation getDocumentation(final String apiName) {
        ensureDocumentationReady();
        for (ControllerDocumentation documentation : resourceDocumentationLookup.values()) {
            if (documentation.matchesName(apiName)) {
                return documentation;
            }
        }
        DocumentationReader.log.error("Could not find a matching resource for api with name '" + apiName + "'");
        return null;
    }

    public final Documentation getDocumentation() {
        ensureDocumentationReady();
        return documentation;
    }

    private synchronized void ensureDocumentationReady() {
        if (!isMappingBuilt) {
            buildMappingDocuments(context);
            isMappingBuilt = true;
        }
    }
    
    public final String stripRequestMappingRegex( final String inputUri ) {
        if ( inputUri == null || inputUri.isEmpty() ) {
            return inputUri;
        }

        // short-circuit pattern matching if there are no parameters.
        if ( inputUri.indexOf('{') < 0 ) { return inputUri; }

        Matcher m = requestMappingURIRegex.matcher(inputUri);
        String uriFormat = m.replaceAll("{%s}");
        m.reset();    //replaceAll changes the matcher's state. Reset before finding the matching groups.
        List<String> paramNames = new ArrayList<String>();
        while ( m.find() ) {
            paramNames.add(m.group(1).split(":")[0]);
        }

        String result = String.format(uriFormat, paramNames.toArray());
//     DocumentationReader.log.debug("Converted uri pattern from " + inputUri + " to " + result);
        return result;
    }

}