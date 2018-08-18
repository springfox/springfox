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

package springfox.documentation.spring.web.paths;

import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.PathProvider;

import static springfox.documentation.spring.web.paths.Paths.*;


public class DefaultPathProvider implements PathProvider {

  /**
   * The base path to the swagger api documentation.
   * <p>
   * Typically docs are served from &lt;yourApp&gt;/api-docs so a relative resourceListing path will omit the api-docs
   * segment.
   * E.g.
   * Relative: "path": "/"
   * Absolute: "path": "http://localhost:8080/api-docs"
   *
   * @return the documentation base path
   */
  protected String getDocumentationPath() {
    return ROOT;
  }

  /**
   * The relative path to the operation, from the basePath, which this operation describes.
   * The value SHOULD be in a relative (URL) path format.
   * <p>
   * Includes the apiResourcePrefix
   *
   * @param operationPath operation path
   * @return the relative path to the api operation
   */
  @Override
  public String getOperationPath(String operationPath) {
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath("/");
    return removeAdjacentForwardSlashes(uriComponentsBuilder.path(operationPath).build().toString());
  }

  /**
   * Corresponds to the path attribute of a swagger Resource Object (within a Resource  Listing).
   * <p>
   * This method builds a URL based off of
   *
   * @param groupName      the group name for this Resource Object e.g. 'default'
   * @param apiDeclaration the identifier for the api declaration e.g 'business-controller'
   * @return the resource listing path
   * @see DefaultPathProvider#getDocumentationPath()
   * by appending the swagger group and apiDeclaration
   */
  @Override
  public String getResourceListingPath(String groupName, String apiDeclaration) {
    String candidate = agnosticUriComponentBuilder(getDocumentationPath())
        .pathSegment(groupName, apiDeclaration)
        .build()
        .toString();
    return removeAdjacentForwardSlashes(candidate);
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


