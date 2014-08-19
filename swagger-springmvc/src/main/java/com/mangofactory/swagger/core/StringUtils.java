package com.mangofactory.swagger.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.*;

public class StringUtils {
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

  public static String maybeChompLeadingSlash(String anyString) {
    if (isNullOrEmpty(anyString) || !anyString.startsWith("/")) {
      return anyString;
    }
    return anyString.replaceFirst("^/", "");
  }

  public static String maybeChompTrailingSlash(String anyString) {
    if (isNullOrEmpty(anyString) || !anyString.endsWith("/")) {
      return anyString;
    }
    return anyString.replaceFirst("/$", "");
  }


  public static String firstPathSegment(String anyString) {
    if (isNullOrEmpty(anyString)) {
      return anyString;
    }
    Matcher matcher = FIRST_PATH_FRAGMENT_REGEX.matcher(anyString);
    if (matcher.find()) {
      return maybeChompTrailingSlash(matcher.group());
    }
    return anyString;
  }
}
