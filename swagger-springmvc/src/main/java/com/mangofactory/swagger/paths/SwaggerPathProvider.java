package com.mangofactory.swagger.paths;

import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import static org.apache.commons.lang.StringUtils.*;

public abstract class SwaggerPathProvider {
  /**
   * e.g the api endpoint resides at  http://myapp.com:8080/<contextPath>/api/v1/businesses
   * Should return api/v1 - no leading or trailing slashes
   * <p/>
   * Typically needed when your web.xml has a mapping to dispatcher servlet like:
   * <url-pattern>/api/v1/*</url-pattern>
   * <p/>
   * and the actual spring request mappings looks like:
   * '@RequestMapping(value = "/businesses/{businessId}")'
   *
   * @return the resource prefix of the api endpoint
   */
  private String apiResourcePrefix = "";

  public String getApiResourcePrefix() {
    return apiResourcePrefix;
  }

  public void setApiResourcePrefix(String apiResourcePrefix) {
    Assert.notNull(apiResourcePrefix);
    Assert.isTrue(!apiResourcePrefix.startsWith("/"));
    Assert.isTrue(!apiResourcePrefix.endsWith("/"));
    this.apiResourcePrefix = apiResourcePrefix;
  }

  /**
   * For relative SwaggerPathProviders this is typically '/' meaning relative to the swagger ui page serving the
   * documentation. The swagger specification recommends that this should be an absolute URL.
   * <p/>
   * Corresponds to the base path attribute of a swagger api declaration.
   * This is the actual base path serving the api (not the swagger documentation)
   *
   * @return the applications base uri
   */
  protected abstract String applicationPath();

  /**
   * The base path to the swagger api documentation.
   * <p/>
   * Typically docs are served from <yourApp>/api-docs so a relative resourceListing path will omit the api-docs
   * segment.
   * E.g.
   * Relative: "path": "/"
   * Absolute: "path": "http://localhost:8080/api-docs"
   *
   * @return the documentation base path
   */
  protected abstract String getDocumentationPath();

  public String getApplicationBasePath() {
    return applicationPath();
  }

  /**
   * The relative path to the operation, from the basePath, which this operation describes.
   * The value SHOULD be in a relative (URL) path format.
   * <p/>
   * Includes the apiResourcePrefix
   *
   * @param operationPath
   * @return the relative path to the api operation
   * @see this.getApplicationBasePath()
   */
  public String getOperationPath(String operationPath) {
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath("/");
    if (!isBlank(apiResourcePrefix)) {
      uriComponentsBuilder.path(apiResourcePrefix);
    }
    return uriComponentsBuilder.path(operationPath).build().toString();
  }

  /**
   * Corresponds to the path attribute of a swagger Resource Object (within a Resource  Listing).
   * <p/>
   * This method builds a URL based off of @see getDocumentationPath by appending the swagger group and apiDeclaration
   *
   * @param swaggerGroup   the swagger for this Resource Object e.g. 'default'
   * @param apiDeclaration the identifier for the api declaration e.g 'business-controller'
   * @return the resource listing path
   */
  public String getResourceListingPath(String swaggerGroup, String apiDeclaration) {
    return agnosticUriComponentBuilder(getDocumentationPath())
            .pathSegment(swaggerGroup, apiDeclaration)
            .build()
            .toString();
  }

  private UriComponentsBuilder agnosticUriComponentBuilder(String url) {
    UriComponentsBuilder uriComponentsBuilder;
    if (url.startsWith("http")) {
      uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
    } else {
      uriComponentsBuilder = UriComponentsBuilder.fromPath(url);
    }
    return uriComponentsBuilder;
  }
}


