package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.ControllerDocumentation;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;

import static com.mangofactory.swagger.spring.UriExtractor.getClassLevelUri;

public class DocumentationEndPoints {
    public static ControllerDocumentation asDocumentation(Documentation parent, DocumentationEndPoint endPoint,
                                                          ControllerAdapter resource) {
        return new ControllerDocumentation(parent.apiVersion(), parent.swaggerVersion(), parent.basePath(),
                getClassLevelUri(resource.getControllerClass()));
    }
}
