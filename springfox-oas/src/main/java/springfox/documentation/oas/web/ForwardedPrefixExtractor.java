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
 */

/*
 * ATTRIBUTION: Taken from spring framework codebase
 */

package springfox.documentation.oas.web;

import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.function.Supplier;

/**
 * Responsible for the contextPath, requestURI, and requestURL with forwarded
 * headers in mind, and also taking into account changes to the path of the
 * underlying delegate request (e.g. on a Servlet FORWARD).
 */
class ForwardedPrefixExtractor {

  private final Supplier<HttpServletRequest> delegate;

  private final UrlPathHelper pathHelper;

  private final String baseUrl;

  private String actualRequestUri;

  private final String forwardedPrefix;

  private String requestUri;

  private String requestUrl;


  ForwardedPrefixExtractor(
      Supplier<HttpServletRequest> delegateRequest,
      UrlPathHelper pathHelper,
      String baseUrl) {

    this.delegate = delegateRequest;
    this.pathHelper = pathHelper;
    this.baseUrl = baseUrl;
    this.actualRequestUri = delegateRequest.get().getRequestURI();

    this.forwardedPrefix = initForwardedPrefix(delegateRequest.get());
    this.requestUri = initRequestUri();
    this.requestUrl = initRequestUrl(); // Keep the order: depends on requestUri
  }

  private static String initForwardedPrefix(HttpServletRequest request) {
    String result = null;
    Enumeration<String> names = request.getHeaderNames();
    while (names.hasMoreElements()) {
      String name = names.nextElement();
      if ("X-Forwarded-Prefix".equalsIgnoreCase(name)) {
        result = request.getHeader(name);
      }
    }
    if (result != null) {
      while (result.endsWith("/")) {
        result = result.substring(0, result.length() - 1);
      }
    }
    return result;
  }

  private String initRequestUri() {
    if (this.forwardedPrefix != null) {
      return this.forwardedPrefix + this.pathHelper.getPathWithinApplication(this.delegate.get());
    }
    return null;
  }

  private String initRequestUrl() {
    return this.baseUrl + (this.requestUri != null ? this.requestUri : this.delegate.get().getRequestURI());
  }


  public String getContextPath() {
    return this.forwardedPrefix == null ? this.delegate.get().getContextPath() : this.forwardedPrefix;
  }

  public String getRequestUri() {
    if (this.requestUri == null) {
      return this.delegate.get().getRequestURI();
    }
    recalculatePathsIfNecessary();
    return this.requestUri;
  }

  public StringBuffer getRequestUrl() {
    recalculatePathsIfNecessary();
    return new StringBuffer(this.requestUrl);
  }

  private void recalculatePathsIfNecessary() {
    if (!this.actualRequestUri.equals(this.delegate.get().getRequestURI())) {
      // Underlying path change (e.g. Servlet FORWARD).
      this.actualRequestUri = this.delegate.get().getRequestURI();
      this.requestUri = initRequestUri();
      this.requestUrl = initRequestUrl(); // Keep the order: depends on requestUri
    }
  }
}
