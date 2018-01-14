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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.*;

public class Paths {
  private static final Pattern FIRST_PATH_FRAGMENT_REGEX = Pattern.compile("^([/]?[\\w\\-\\.]+[/]?)");

  private Paths() {
    throw new UnsupportedOperationException();
  }

  public static String splitCamelCase(String s, String separator) {
    if (isNullOrEmpty(s)) {
      return "";
    }
    return s.replaceAll(
            String.format("%s|%s|%s",
                    "(?<=[A-Z])(?=[A-Z][a-z])",
                    "(?<=[^A-Z])(?=[A-Z])",
                    "(?<=[A-Za-z])(?=[^A-Za-z])"
            ),
            separator
    );
  }

  public static String stripSlashes(String stringWithSlashes) {
    return stringWithSlashes.replace("/", "").replace("\\", "");
  }

  public static String maybeChompLeadingSlash(String path) {
    if (isNullOrEmpty(path) || !path.startsWith("/")) {
      return path;
    }
    return path.replaceFirst("^/", "");
  }

  public static String maybeChompTrailingSlash(String path) {
    if (isNullOrEmpty(path) || !path.endsWith("/")) {
      return path;
    }
    return path.replaceFirst("/$", "");
  }


  public static String firstPathSegment(String path) {
    if (isNullOrEmpty(path)) {
      return path;
    }
    Matcher matcher = FIRST_PATH_FRAGMENT_REGEX.matcher(path);
    if (matcher.find()) {
      return maybeChompTrailingSlash(matcher.group());
    }
    return path;
  }

  /**
   * Gets a uri friendly path from a request mapping pattern.
   * Typically involves removing any regex patterns or || conditions from a spring request mapping
   * This method will be called to resolve every request mapping endpoint.
   * A good extension point if you need to alter endpoints by adding or removing path segments.
   * Note: this should not be an absolute  uri
   *
   * @param requestMappingPattern
   * @return the request mapping endpoint
   */
  public static String sanitizeRequestMappingPattern(String requestMappingPattern) {
    String result = requestMappingPattern;
    //remove regex portion '/{businessId:\\w+}'
    result = result.replaceAll("\\{([^}]+?):([^/{}]|\\{[\\d,]+})+}", "{$1}");
    return result.isEmpty() ? "/" : result;
  }

  public static String removeAdjacentForwardSlashes(String candidate) {
    return candidate.replaceAll("(?<!(http:|https:))//", "/");
  }
}
