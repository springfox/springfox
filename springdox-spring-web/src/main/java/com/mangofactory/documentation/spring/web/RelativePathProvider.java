package com.mangofactory.documentation.spring.web;

import javax.servlet.ServletContext;

import static com.google.common.base.Strings.*;

public class RelativePathProvider extends AbstractPathProvider {
  public static final String ROOT = "/";
  private final ServletContext servletContext;

  public RelativePathProvider(ServletContext servletContext) {
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
