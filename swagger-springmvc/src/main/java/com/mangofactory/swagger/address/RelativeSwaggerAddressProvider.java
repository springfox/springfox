package com.mangofactory.swagger.address;

import javax.servlet.ServletContext;

public class RelativeSwaggerAddressProvider extends SwaggerAddressProvider {

  public RelativeSwaggerAddressProvider(ServletContext servletContext) {
    super(servletContext);
  }

  @Override
  protected String host() {
    return null;
  }
}
