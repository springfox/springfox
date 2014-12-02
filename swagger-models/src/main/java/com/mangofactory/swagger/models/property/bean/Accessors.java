package com.mangofactory.swagger.models.property.bean;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Optional;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.*;

class Accessors {
  private static Pattern getter = Pattern.compile("^get([a-zA-Z_0-9].*)");
  private static Pattern isGetter = Pattern.compile("^is([a-zA-Z_0_9].*)");
  private static Pattern setter = Pattern.compile("^set([a-zA-Z_0-9].*)");

  public static boolean isGetter(Method method) {
    if (method.getParameterTypes().length == 0) {
      return isGetterThatIsNotAVoidMethod(method)
              || isBooleanGetterMethod(method)
              || getterAnnotation(method).isPresent();
    }
    return false;
  }

  private static boolean isGetterThatIsNotAVoidMethod(Method method) {
    return getter.matcher(method.getName()).find() &&
            !method.getReturnType().equals(void.class);
  }

  private static boolean isBooleanGetterMethod(Method method) {
    return isGetter.matcher(method.getName()).find() && method.getReturnType().equals(boolean.class);
  }

  private static Optional<JsonGetter> getterAnnotation(Method method) {
    return Optional.fromNullable(AnnotationUtils.findAnnotation(method, JsonGetter.class));
  }

  private static Optional<JsonSetter> setterAnnotation(Method method) {
    return Optional.fromNullable(AnnotationUtils.findAnnotation(method, JsonSetter.class));
  }

  public static boolean isSetter(Method method) {
    return isSetterMethod(method) || setterAnnotation(method).isPresent();
  }

  private static boolean isSetterMethod(Method method) {
    return method.getParameterTypes().length == 1 && setter.matcher(method.getName()).find();
  }

  public static String toCamelCase(String s) {
    return s.substring(0, 1).toLowerCase() +
            s.substring(1);
  }

  public static String propertyName(Method method) {
    Optional<JsonGetter> jsonGetterAnnotation = getterAnnotation(method);
    if (jsonGetterAnnotation.isPresent() && !isNullOrEmpty(jsonGetterAnnotation.get().value())){
      return jsonGetterAnnotation.get().value();
    }
    Optional<JsonSetter> jsonSetterAnnotation = setterAnnotation(method);
    if (jsonSetterAnnotation.isPresent() && !isNullOrEmpty(jsonSetterAnnotation.get().value())) {
      return jsonSetterAnnotation.get().value();
    }
    Matcher matcher = getter.matcher(method.getName());
    if (matcher.find()) {
      return toCamelCase(matcher.group(1));
    }
    matcher = isGetter.matcher(method.getName());
    if (matcher.find()) {
      return toCamelCase(matcher.group(1));
    }
    matcher = setter.matcher(method.getName());
    if (matcher.find()) {
      return toCamelCase(matcher.group(1));
    }
    return "";
  }
}
