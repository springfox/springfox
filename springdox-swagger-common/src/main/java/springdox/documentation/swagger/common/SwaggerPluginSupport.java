package springdox.documentation.swagger.common;

import springdox.documentation.spi.DocumentationType;

public class SwaggerPluginSupport {
  private SwaggerPluginSupport() {
    throw new UnsupportedOperationException();
  }

  public static final String DOCUMENTATION_BASE_PATH = "/v1/api-docs";

  public static boolean pluginDoesApply(DocumentationType documentationType) {
    return DocumentationType.SWAGGER_12.equals(documentationType) || DocumentationType.SWAGGER_2.equals(documentationType);
  }
}
