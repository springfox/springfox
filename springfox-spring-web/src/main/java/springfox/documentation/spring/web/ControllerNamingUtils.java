/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.util.UriUtils;

import static springfox.documentation.spring.web.paths.Paths.*;

public class ControllerNamingUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(ControllerNamingUtils.class);
  private static final String ISO_8859_1 = "ISO-8859-1";

  private ControllerNamingUtils() {
    throw new UnsupportedOperationException();
  }

  public static String pathRoot(String requestPattern) {
    Assert.notNull(requestPattern, "Request pattern is required");
    Assert.hasText(requestPattern, "Request pattern is not empty");
    LOGGER.debug("Resolving path root for {}", requestPattern);
    String adjustedPattern = requestPattern.startsWith("/") ? requestPattern : "/" + requestPattern;
    int idx = adjustedPattern.indexOf("/", 1);
    if (idx > -1) {
      return adjustedPattern.substring(0, idx);
    }
    return adjustedPattern;
  }

  public static String pathRootEncoded(String requestPattern) {
    return encode(pathRoot(requestPattern));
  }

  public static String encode(String path) {
    return UriUtils.encodePath(path, ISO_8859_1);
  }

  public static String decode(String path) {
    try {
      return UriUtils.decode(path, ISO_8859_1);
    } catch (Exception e) {
      LOGGER.error("Could not decode:" + path, e);
    }
    return path;
  }


  public static String controllerNameAsGroup(HandlerMethod handlerMethod) {
    Class<?> controllerClass = handlerMethod.getBeanType();
    return splitCamelCase(controllerClass.getSimpleName(), "-")
        .replace("/", "")
        .toLowerCase();
  }
}
