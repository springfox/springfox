package com.mangofactory.documentation.swagger.common;

import com.mangofactory.documentation.spi.DocumentationType;

import static com.mangofactory.documentation.spi.DocumentationType.*;

public class SwaggerPluginSupport {
  private SwaggerPluginSupport() {
    throw new UnsupportedOperationException();
  }

  public static final String DOCUMENTATION_BASE_PATH = "/v1/api-docs";

  public static boolean pluginDoesApply(DocumentationType documentationType) {
    return SWAGGER_12.equals(documentationType) || SWAGGER_2.equals(documentationType);
  }
}
