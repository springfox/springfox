package com.mangofactory.documentation.spring.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.*;

public class Paths {
  private static final Pattern FIRST_PATH_FRAGMENT_REGEX = Pattern.compile("^([/]?[\\w\\-\\.]+[/]?)");

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
}
