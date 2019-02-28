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

package springfox.documentation.schema.property.bean;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Optional.*;
import static org.springframework.util.StringUtils.*;

public class Accessors {
  private static Pattern getter = Pattern.compile("^get([a-zA-Z_0-9].*)");
  private static Pattern isGetter = Pattern.compile("^is([a-zA-Z_0_9].*)");
  private static Pattern setter = Pattern.compile("^set([a-zA-Z_0-9].*)");
  private Accessors() {
    throw new UnsupportedOperationException();
  }

  public static boolean maybeAGetter(Method method) {
    if (method.getParameterTypes().length == 0) {
      return notAVoidMethod(method);
    }
    return false;
  }

  private static boolean notAVoidMethod(Method method) {
    return !method.getReturnType().equals(void.class);
  }

  public static boolean isSetter(Method method) {
    return isSetterMethod(method) || setterAnnotation(method).isPresent();
  }

  public static String toCamelCase(String s) {
    return s.substring(0, 1).toLowerCase() +
            s.substring(1);
  }

  @SuppressWarnings({"CyclomaticComplexity", "NPathComplexity"})
  public static String propertyName(Method method) {
    Optional<JsonGetter> jsonGetterAnnotation = getterAnnotation(method);
    if (jsonGetterAnnotation.isPresent() && !isEmpty(jsonGetterAnnotation.get().value())) {
      return jsonGetterAnnotation.get().value();
    }
    Optional<JsonSetter> jsonSetterAnnotation = setterAnnotation(method);
    if (jsonSetterAnnotation.isPresent() && !isEmpty(jsonSetterAnnotation.get().value())) {
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

  private static Optional<JsonGetter> getterAnnotation(Method method) {
    return ofNullable(AnnotationUtils.findAnnotation(method, JsonGetter.class));
  }

  private static Optional<JsonSetter> setterAnnotation(Method method) {
    return ofNullable(AnnotationUtils.findAnnotation(method, JsonSetter.class));
  }

  private static boolean isSetterMethod(Method method) {
    return maybeASetter(method) && setter.matcher(method.getName()).find();
  }

  public static boolean maybeASetter(Method method) {
    return method.getParameterTypes().length == 1;
  }
}
