package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.models.DocumentationSchemaProvider;
import com.wordnik.swagger.core.Documentation;

public class DocumentationEndPoints {
    public static ControllerDocumentation asDocumentation(Documentation parent, String resourcePath,
                                                          DocumentationSchemaProvider schemaProvider) {
        return new ControllerDocumentation(parent.apiVersion(), parent.swaggerVersion(), parent.basePath(),
                resourcePath, schemaProvider);
    }
}
