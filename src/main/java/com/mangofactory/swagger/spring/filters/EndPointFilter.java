package com.mangofactory.swagger.spring.filters;

import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.spring.UriBuilder;
import com.wordnik.swagger.core.DocumentationEndPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import static com.mangofactory.swagger.spring.Descriptions.*;

@Slf4j
public class EndPointFilter implements Filter<DocumentationEndPoint> {
    @Override
    public void apply(FilterContext<DocumentationEndPoint> context) {
        DocumentationEndPoint doc = context.subject();
        Class<?> controllerClass = context.get("controllerClass");
        HandlerMethod handlerMethod = context.get("handlerMethod");
        String documentationUri = new UriBuilder(maybeGetUriFromRequestMappingAnnotation(controllerClass))
                  .appendPath(maybeGetUriFromRequestMappingAnnotation(handlerMethod)).toString();
        doc.setPath(documentationUri);
        doc.setDescription(getDescription(controllerClass));
    }

    private String getDescription(Class<?> controllerClass) {
        return splitCamelCase(controllerClass.getSimpleName());
    }

    public String maybeGetUriFromRequestMappingAnnotation(Class<?> controllerClass) {
        RequestMapping requestMapping = controllerClass.getAnnotation(RequestMapping.class);
        String requestUri = resolveRequestUri(requestMapping);
        if (requestUri == null) {
            return "";
        }
        return requestUri;
    }

    public String maybeGetUriFromRequestMappingAnnotation(HandlerMethod handlerMethod) {
        String requestUri = resolveRequestUri(handlerMethod.getMethodAnnotation(RequestMapping.class));
        if (requestUri == null) {
            return "";
        }
        return requestUri;
    }

    private String resolveRequestUri(RequestMapping requestMapping) {
        if (requestMapping == null) {
            EndPointFilter.log.info("Class {} has no @RequestMapping", requestMapping);
            return null;
        }
        String[] requestUris = requestMapping.value();
        if (requestUris == null || requestUris.length == 0) {
            EndPointFilter.log.info("Class {} contains a @RequestMapping, but could not resolve the uri", requestMapping);
            return null;
        }
        if (requestUris.length > 1) {
            EndPointFilter.log.info("Class {} contains a @RequestMapping with multiple uri's. Only the first one will be documented.",
                    requestMapping);
        }
        return requestUris[0];
    }
}
