package com.mangofactory.swagger.spring;

import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;

public class UriBuilder {

    private StringBuilder sb = new StringBuilder();
    boolean queryParamAdded = false;
    public UriBuilder() {
    }

    public UriBuilder(String uri) {
        sb.append(uri);
    }

    public static String fromOperation(String basePath, DocumentationOperation operation) {
        UriBuilder builder = new UriBuilder(basePath);
        for (DocumentationParameter parameter : operation.getParameters()) {
            if ("path".equals(parameter.getParamType())) {
                builder.appendPath(parameter.name());
            } else if ("query".equals(parameter.name())) {
                builder.appendQueryString(parameter.name());
            }
        }
        return builder.toString();
    }

    public UriBuilder appendPath(String segment) {
        if (!sb.toString().endsWith("/")) {
            sb.append("/");
        }
        if (segment.startsWith("/")) {
            sb.append(segment.substring(1));
        } else {
            sb.append(segment);
        }
        return this;
    }

    private void appendQueryString(String parameterName) {
        if (queryParamAdded) {
            sb.append(String.format("&%s={%s}", parameterName, parameterName));
        } else {
            sb.append(String.format("?%s={%s}", parameterName, parameterName));
        }
    }

    public String toString() {
        return sb.toString();
    }
}
