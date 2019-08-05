/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
package springfox.documentation.swagger.common;

import springfox.documentation.common.SpringVersion;
import springfox.documentation.service.PathAdjuster;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.util.StringUtils.*;
import static springfox.documentation.common.SpringVersionCapability.*;

public class XForwardPrefixPathAdjuster implements PathAdjuster {
  static final String X_FORWARDED_PREFIX = "X-Forwarded-Prefix";

  private final HttpServletRequest request;
  private final SpringVersion springVersion;

  public XForwardPrefixPathAdjuster(HttpServletRequest request) {
    this(request, new SpringVersion());
  }

  XForwardPrefixPathAdjuster(HttpServletRequest request, SpringVersion springVersion) {
    this.request = request;
    this.springVersion = springVersion;
  }

  @Override
  public String adjustedPath(String path) {
    String prefix = request.getHeader(X_FORWARDED_PREFIX);
    if (prefix != null) {
      prefix = isEmpty(prefix) ? "/" : prefix;
      if (!supportsXForwardPrefixHeader(springVersion.getVersion())) {
        return prefix + path;
      } else {
        return prefix;
      }
    } else {
      return path;
    }
  }
}
