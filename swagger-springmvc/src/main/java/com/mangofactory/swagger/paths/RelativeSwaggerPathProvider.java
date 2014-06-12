package com.mangofactory.swagger.paths;

public class  RelativeSwaggerPathProvider extends SwaggerPathProvider {
    public static final String ROOT = "/";

    @Override
    protected String applicationPath() {
        return ROOT;
    }

    @Override
    protected String getDocumentationPath() {
        return ROOT;
    }
}
