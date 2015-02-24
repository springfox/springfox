package com.mangofactory.documentation.swagger.common;

import com.mangofactory.documentation.spi.DocumentationType;

public class SwaggerPluginSupport {
  private SwaggerPluginSupport() {
    throw new UnsupportedOperationException();
  }

  public static boolean pluginDoesApply(DocumentationType documentationType) {
    return DocumentationType.SWAGGER_12.equals(documentationType);
  }
}
