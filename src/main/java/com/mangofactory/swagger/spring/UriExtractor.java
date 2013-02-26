package com.mangofactory.swagger.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.AnnotatedElement;

import static com.google.common.base.Strings.*;

@Slf4j
public class UriExtractor {
    public static String getClassLevelUri(AnnotatedElement controllerClass) {
        String classLevelUri = resolveRequestUri(controllerClass);
        if (isNullOrEmpty(classLevelUri)) {
            classLevelUri = "/";
        }
        if (!classLevelUri.startsWith("/")) {
            classLevelUri = String.format("/%s", classLevelUri);
        }
        UriBuilder builder = new UriBuilder();
        maybeAppendPath(builder, classLevelUri);
        return builder.toString();
    }

    public static String getMethodLevelUri(AnnotatedElement controllerClass, HandlerMethod handlerMethod) {
        String classLevelUri = resolveRequestUri(controllerClass);
        if (isNullOrEmpty(classLevelUri)) {
            classLevelUri = "/";
        }
        if (!classLevelUri.startsWith("/")) {
            classLevelUri = String.format("/%s", classLevelUri);
        }
        String methodLevelUri = resolveRequestUri(handlerMethod.getMethod());
        UriBuilder builder = new UriBuilder();

        maybeAppendPath(builder, classLevelUri);
        maybeAppendPath(builder, methodLevelUri);
        return builder.toString();
    }

    private static void maybeAppendPath(UriBuilder builder, String toAppendUri) {
        if (!isNullOrEmpty(toAppendUri)) {
            builder.appendPath(toAppendUri);
        }
    }

    protected static String resolveRequestUri(AnnotatedElement annotatedElement) {
        RequestMapping requestMapping = annotatedElement.getAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            log.info("Class {} has no @RequestMapping", annotatedElement);
            return null;
        }
        String[] requestUris = requestMapping.value();
        if (requestUris == null || requestUris.length == 0) {
            log.info("Class {} contains a @RequestMapping, but could not resolve the uri", annotatedElement);
            return null;
        }
        if (requestUris.length > 1) {
            log.info("Class {} contains a @RequestMapping with multiple uri's. Only the first one will be documented.",
                    annotatedElement);
        }
        return requestUris[0];
    }
}
