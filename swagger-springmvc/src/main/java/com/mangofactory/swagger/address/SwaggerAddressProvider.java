package com.mangofactory.swagger.address;

import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletContext;

public abstract class SwaggerAddressProvider {

  protected ServletContext servletContext;
  private String basePath;

  public SwaggerAddressProvider(ServletContext servletContext) {
    this.servletContext = servletContext;
  }

  protected abstract String host();

  public String getBasePath() {
    return basePath;
  }

  /**
   * Allows for prefixing your api endpoints.
   * Typically use is when your web.xml has a dispatcher servlet mapping to your api paths which is not exposed by
   * spring and hence this library cannot determine the real path.
   * These kind of servlet mappings are typically used for url based security.
   *
   * e.g:
   * <servlet-mapping>
   *  <servlet-name>api-dispatcher</servlet-name>
   *  <url-pattern>/api/v1/*</url-pattern>
   * </servlet-mapping>
   *
   * @param basePath
   */
  public void setBasePath(String basePath) {
    Assert.notNull(basePath);
    Assert.isTrue(basePath.startsWith("/"), "basePath must begin with a forward slash");
    Assert.isTrue(!basePath.endsWith("/"), "basePath must not end with a forward slash");
    this.basePath = basePath;
  }


  public String getHost() {
    return host();
  }

  /**
   * The relative path to the operation, from the basePath, which this operation describes.
   * The value MUST be in a relative (URL) path format.
   * <p/>
   * Includes the basePath
   *
   * @param operationPath
   * @return the relative path to the api operation
   */
  public String getOperationPath(String operationPath) {
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath("/");
    return uriComponentsBuilder.path(operationPath).build().toString();
  }

}
