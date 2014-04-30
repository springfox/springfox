package com.mangofactory.swagger.paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletContext;

public class RelativeSwaggerPathProvider extends SwaggerPathProvider {
    @Autowired
    private ServletContext servletContext;

    @Override
    protected String applicationPath() {
        return UriComponentsBuilder
                .fromPath(null)
                .path(servletContext.getContextPath())
                .build()
                .toString();
    }
}
