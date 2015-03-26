/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.spring.web;

import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.PathProvider;


public abstract class AbstractPathProvider implements PathProvider {
//  /**
//   * e.g the api endpoint resides at  http://myapp.com:8080/<contextPath>/api/v1/businesses
//   * Should return api/v1 - no leading or trailing slashes
//   *
//   * Typically needed when your web.xml has a mapping to dispatcher servlet like:
//   * <url-pattern>/api/v1/*</url-pattern>
//   *
//   * and the actual spring request mappings looks like:
//   * '@RequestMapping(value = "/businesses/{businessId}")'
//   *
//   * @return the resource prefix of the api endpoint
//   */
//  private String apiResourcePrefix = "";
//
//  public String getApiResourcePrefix() {
//    return apiResourcePrefix;
//  }
//
//  public void setApiResourcePrefix(String apiResourcePrefix) {
//    Assert.notNull(apiResourcePrefix);
//    Assert.isTrue(!apiResourcePrefix.startsWith("/"));
//    Assert.isTrue(!apiResourcePrefix.endsWith("/"));
//    this.apiResourcePrefix = apiResourcePrefix;
//  }

  /**
   * For relative PathProviders this is typically '/' meaning relative to the swagger ui page serving the
   * documentation. The swagger specification recommends that this should be an absolute URL.
   * 
   * Corresponds to the base path attribute of a swagger api declaration.
   * This is the actual base path serving the api (not the swagger documentation)
   *
   * @return the applications base uri
   */
  protected abstract String applicationPath();

  /**
   * The base path to the swagger api documentation.
   * 
   * Typically docs are served from &lt;yourApp&gt;/api-docs so a relative resourceListing path will omit the api-docs
   * segment.
   * E.g.
   * Relative: "path": "/"
   * Absolute: "path": "http://localhost:8080/api-docs"
   *
   * @return the documentation base path
   */
  protected abstract String getDocumentationPath();

  @Override
  public String getApplicationBasePath() {
    return applicationPath();
  }

  /**
   * The relative path to the operation, from the basePath, which this operation describes.
   * The value SHOULD be in a relative (URL) path format.
   * 
   * Includes the apiResourcePrefix
   *
   * @param operationPath
   * @return the relative path to the api operation
   * @see AbstractPathProvider#getApplicationBasePath()
   */
  @Override
  public String getOperationPath(String operationPath) {
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath("/");
//    if (StringUtils.hasText(apiResourcePrefix)) {
//      uriComponentsBuilder.path(apiResourcePrefix);
//    }
    return Paths.sanitiseUrl(uriComponentsBuilder.path(operationPath).build().toString());
  }

  /**
   * Corresponds to the path attribute of a swagger Resource Object (within a Resource  Listing).
   * 
   * This method builds a URL based off of
   * @see AbstractPathProvider#getDocumentationPath()
   * by appending the swagger group and apiDeclaration
   *
   * @param groupName   the group name for this Resource Object e.g. 'default'
   * @param apiDeclaration the identifier for the api declaration e.g 'business-controller'
   * @return the resource listing path
   */
  @Override
  public String getResourceListingPath(String groupName, String apiDeclaration) {
    String candidate = agnosticUriComponentBuilder(getDocumentationPath())
            .pathSegment(groupName, apiDeclaration)
            .build()
            .toString();
    return Paths.sanitiseUrl(candidate);
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


