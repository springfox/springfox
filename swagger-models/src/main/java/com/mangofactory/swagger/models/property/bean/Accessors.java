package com.mangofactory.swagger.models.property.bean;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Accessors {
  private static Pattern getter = Pattern.compile("^get([a-zA-Z_0-9].*)");
  private static Pattern isGetter = Pattern.compile("^is([a-zA-Z_0_9].*)");
  private static Pattern setter = Pattern.compile("^set([a-zA-Z_0-9].*)");

  public static boolean isGetter(Method method) {
    if (method.getParameterTypes().length == 0) {
      if (getter.matcher(method.getName()).find() &&
              !method.getReturnType().equals(void.class)) {
        return true;
      }
      if (isGetter.matcher(method.getName()).find() &&
              method.getReturnType().equals(boolean.class)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isSetter(Method method) {
    return method.getParameterTypes().length == 1 &&
            setter.matcher(method.getName()).find();
  }

  public static String toCamelCase(String s) {
    return s.substring(0, 1).toLowerCase() +
            s.substring(1);
  }

  public static String propertyName(String methodName) {
    Matcher matcher = getter.matcher(methodName);
    if (matcher.find()) {
      return toCamelCase(matcher.group(1));
    }
    matcher = isGetter.matcher(methodName);
    if (matcher.find()) {
      return toCamelCase(matcher.group(1));
    }
    matcher = setter.matcher(methodName);
    if (matcher.find()) {
      return toCamelCase(matcher.group(1));
    }
    return "";
  }
}
