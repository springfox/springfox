package com.mangofactory.swagger.paths;

import javax.servlet.ServletContext;

public class  RelativeSwaggerPathProvider extends SwaggerPathProvider {
    public static final String ROOT = "/";
  private final ServletContext servletContext;

  public RelativeSwaggerPathProvider(ServletContext servletContext) {
    super();
    this.servletContext = servletContext;
  }

  @Override
    protected String applicationPath() {
        return servletContext.getContextPath();
    }

    @Override
    protected String getDocumentationPath() {
        return ROOT;
    }
}
