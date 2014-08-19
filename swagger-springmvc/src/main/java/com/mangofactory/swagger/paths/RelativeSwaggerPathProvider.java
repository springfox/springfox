package com.mangofactory.swagger.paths;

import javax.servlet.ServletContext;

import static com.google.common.base.Strings.*;

public class RelativeSwaggerPathProvider extends SwaggerPathProvider {
  public static final String ROOT = "/";
  private final ServletContext servletContext;

  public RelativeSwaggerPathProvider(ServletContext servletContext) {
    super();
    this.servletContext = servletContext;
  }

  @Override
  protected String applicationPath() {
    return isNullOrEmpty(servletContext.getContextPath()) ? ROOT : servletContext.getContextPath();
  }

  @Override
  protected String getDocumentationPath() {
    return ROOT;
  }
}
