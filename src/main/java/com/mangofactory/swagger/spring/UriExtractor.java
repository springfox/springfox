package com.mangofactory.swagger.spring;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.spring.Descriptions.*;
import static java.lang.String.*;

public class UriExtractor {
    private static final Logger LOG = LogManager.getLogger(UriExtractor.class);

    public static List<String> controllerUris(Class<?> controllerClass) {
        List<String> controllerUris = newArrayList();
        List<String> classLevelUris = resolveRequestUri(controllerClass, requestMapping(controllerClass));
        String defaultUri = splitCamelCase(controllerClass.getSimpleName(), "-").toLowerCase();
        if (classLevelUris.isEmpty()) {
            classLevelUris.add("/" + defaultUri);
        }
        for(String classLevelUri: classLevelUris) {
            if (isNullOrEmpty(classLevelUri)) {
                classLevelUri = "/" + defaultUri;
            }
            if (!classLevelUri.startsWith("/")) {
                classLevelUri = format("/%s", classLevelUri);
            }
            UriBuilder builder = new UriBuilder();
            maybeAppendPath(builder, classLevelUri);
            controllerUris.add(builder.toString());
        }
        return controllerUris;
    }

    public static List<String> methodUris(Class<?> controllerClass, HandlerMethod handlerMethod) {
        List<String> classLevelUris = resolveRequestUri(controllerClass, requestMapping(controllerClass));
        if (classLevelUris.isEmpty()) {
            classLevelUris.add("/");
        }
        List<String> methodLevelUris = newArrayList();
        for(String classLevelUri: classLevelUris) {
            if (!classLevelUri.startsWith("/")) {
                classLevelUri = format("/%s", classLevelUri);
            }
            UriBuilder builder = new UriBuilder();
            maybeAppendPath(builder, classLevelUri);
            for(String methodLevelUri: resolveRequestUri(controllerClass, requestMapping(handlerMethod.getMethod()))) {
                maybeAppendPath(builder, methodLevelUri);
            }
            methodLevelUris.add(builder.toString());
        }
        return methodLevelUris;
    }

    private static void maybeAppendPath(UriBuilder builder, String toAppendUri) {
        if (!isNullOrEmpty(toAppendUri)) {
            builder.appendPath(toAppendUri);
        }
    }

    private static RequestMapping requestMapping(Class<?> annotated) {
        return AnnotationUtils.findAnnotation(annotated, RequestMapping.class);
    }

    private static RequestMapping requestMapping(AnnotatedElement annotated) {
        return annotated.getAnnotation(RequestMapping.class);
    }

    protected static List<String> resolveRequestUri(Class clazz, RequestMapping requestMapping) {
        if (requestMapping == null) {
            LOG.debug(format("Class %s has no @RequestMapping", clazz));
            return newArrayList();
        }
        String[] requestUris = requestMapping.value();
        if (requestUris == null || requestUris.length == 0) {
            LOG.warn(format("Class %s contains a @RequestMapping, but could not resolve the uri", clazz));
            return newArrayList();
        }
        return newArrayList(requestUris);
    }


}
