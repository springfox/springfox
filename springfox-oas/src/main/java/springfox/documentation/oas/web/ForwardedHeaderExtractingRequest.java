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

/*
 * ATTRIBUTION: Taken from spring framework codebase
 */
package springfox.documentation.oas.web;

import org.springframework.http.HttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Supplier;

class ForwardedHeaderExtractingRequest {

  private final String scheme;
  private final boolean secure;
  private final String host;
  private final int port;
  private final ForwardedPrefixExtractor forwardedPrefixExtractor;

  ForwardedHeaderExtractingRequest(
      HttpServletRequest request,
      UrlPathHelper pathHelper) {

    HttpRequest httpRequest = new ServletServerHttpRequest(request);
    UriComponents uriComponents = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
    int port = uriComponents.getPort();

    this.scheme = uriComponents.getScheme();
    this.secure = "https".equals(this.scheme);
    this.host = uriComponents.getHost();
    this.port = (port == -1 ? (this.secure ? 443 : 80) : port);

    String baseUrl = this.scheme + "://" + this.host + (port == -1 ? "" : ":" + port);
    Supplier<HttpServletRequest> delegateRequest = () -> request;
    this.forwardedPrefixExtractor = new ForwardedPrefixExtractor(delegateRequest, pathHelper, baseUrl);
  }


  public String getScheme() {
    return this.scheme;
  }

  public String getServerName() {
    return this.host;
  }

  public int getServerPort() {
    return this.port;
  }

  public boolean isSecure() {
    return this.secure;
  }

  public String getContextPath() {
    return this.forwardedPrefixExtractor.getContextPath();
  }

  public String getRequestURI() {
    return this.forwardedPrefixExtractor.getRequestUri();
  }

  public StringBuffer getRequestURL() {
    return this.forwardedPrefixExtractor.getRequestUrl();
  }

  public String adjustedRequestURL() {
    return String.format("%s://%s:%s",
        getScheme(),
        getServerName(),
        getServerPort());
  }
}