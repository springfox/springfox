package com.mangofactory.swagger.paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletContext;

public class AbsoluteSwaggerPathProvider extends SwaggerPathProvider {
    @Autowired
    private ServletContext servletContext;

    @Override
    protected String applicationPath() {
        return UriComponentsBuilder
                .fromHttpUrl("http://127.0.0.1:8080")
                .path(servletContext.getContextPath())
                .build()
                .toString();
    }
}
