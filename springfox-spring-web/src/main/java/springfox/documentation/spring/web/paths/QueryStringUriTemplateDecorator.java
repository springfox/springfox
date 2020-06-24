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
package springfox.documentation.spring.web.paths;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.PathDecorator;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.PathContext;

import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;
import static org.springframework.util.StringUtils.*;

/**
 * Since we're no longer using rfc6570
 * @deprecated @since 3.0.0
 */
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE + 60)
@Deprecated
class QueryStringUriTemplateDecorator implements PathDecorator {
  @Override
  public Function<String, String> decorator(final PathContext context) {
    return input -> {
      StringBuilder sb = new StringBuilder(input);
      String prefilled = prefilledQueryParams(context);
      if (!isEmpty(prefilled)) {
        sb.append(requiresContinuation(input) ? "&" : "?");
        sb.append(prefilled);
      }
      Set<String> expressions = queryParamNames(context);
      if (expressions.size() == 0) {
        return sb.toString();
      }
      String prefix = queryTemplatePrefix(input, prefilled);
      String queryTemplate = String.join(",", expressions);
      sb.append(prefix).append(queryTemplate).append("}");
      return sb.toString();
    };
  }

  private String queryTemplatePrefix(
      String input,
      String prefilled) {
    String prefix;
    if (isEmpty(prefilled)) {
      if (requiresContinuation(input)) {
        prefix = "{&";
      } else {
        prefix = "{?";
      }
    } else {
      prefix = "{&";
    }
    return prefix;
  }

  private boolean requiresContinuation(String url) {
    return url.contains("?");
  }

  private Set<String> queryParamNames(PathContext context) {
    return context.getParameters().stream()
        .filter(queryStringParams().and(onlyOneAllowableValue().negate()))
        .map(springfox.documentation.service.Parameter::getName)
        .collect(toCollection(TreeSet::new));
  }

  private String prefilledQueryParams(PathContext context) {
    return String.join(
        "&",
        context.getParameters().stream()
            .filter(onlyOneAllowableValue())
            .map(queryStringWithValue())
            .collect(toCollection(TreeSet::new))).trim();
  }

  private Predicate<springfox.documentation.service.Parameter> onlyOneAllowableValue() {
    return input -> {
      AllowableValues allowableValues = input.getAllowableValues();
      return allowableValues instanceof AllowableListValues
          && ((AllowableListValues) allowableValues).getValues().size() == 1;
    };
  }

  private Predicate<springfox.documentation.service.Parameter> queryStringParams() {
    return input -> "query".equals(input.getParamType());
  }


  private Function<springfox.documentation.service.Parameter, String> queryStringWithValue() {
    return input -> {
      AllowableListValues allowableValues = (AllowableListValues) input.getAllowableValues();
      return String.format(
          "%s=%s",
          input.getName(),
          allowableValues.getValues().get(0).trim());
    };
  }

  @Override
  public boolean supports(DocumentationContext delimiter) {
    return delimiter.isUriTemplatesEnabled();
  }
}
