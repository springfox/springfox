package com.mangofactory.swagger.paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletContext;

public class RelativeSwaggerPathProvider extends SwaggerPathProvider {
    @Autowired
    private ServletContext servletContext;

    public String getAppBasePath() {
        return UriComponentsBuilder
                .fromPath(null)
                .path(servletContext.getContextPath())
                .build()
                .toString();
    }

    public String getSwaggerDocumentationBasePath() {
        return UriComponentsBuilder
                .fromPath(getAppBasePath())
                .pathSegment("api-docs/")
                .build()
                .toString();
    }
}
