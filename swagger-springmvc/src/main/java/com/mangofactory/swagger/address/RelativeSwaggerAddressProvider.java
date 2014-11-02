package com.mangofactory.swagger.address;

import javax.servlet.ServletContext;

import static com.google.common.base.Strings.isNullOrEmpty;

public class RelativeSwaggerAddressProvider extends SwaggerAddressProvider {
  public static final String ROOT = "/";
  private final ServletContext servletContext;

  public RelativeSwaggerAddressProvider(ServletContext servletContext) {
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
