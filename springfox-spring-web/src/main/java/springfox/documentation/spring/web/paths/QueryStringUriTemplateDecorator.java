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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.PathDecorator;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.PathContext;

import java.util.Set;

import static com.google.common.base.Predicates.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.FluentIterable.*;

@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE + 60)
class QueryStringUriTemplateDecorator implements PathDecorator {
  @Override
  public Function<String, String> decorator(final PathContext context) {
    return new Function<String, String>() {
      @Override
      public String apply(String input) {
        StringBuilder sb = new StringBuilder(input);
        String prefilled = prefilledQueryParams(context);
        if (!isNullOrEmpty(prefilled)) {
          sb.append(requiresContinuation(input) ? "&" : "?");
          sb.append(prefilled);
        }
        Set<String> expressions = queryParamNames(context);
        if (expressions.size() == 0) {
          return sb.toString();
        }
        String prefix = queryTemplatePrefix(input, prefilled);
        String queryTemplate = Joiner.on(',').join(expressions);
        sb.append(prefix).append(queryTemplate).append("}");
        return sb.toString();
      }
    };
  }

  private String queryTemplatePrefix(String input, String prefilled) {
    String prefix;
    if (isNullOrEmpty(prefilled)) {
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
    return from(context.getParameters())
        .filter(and(queryStringParams(), not(onlyOneAllowableValue())))
        .transform(paramName())
        .toSet();
  }

  private String prefilledQueryParams(PathContext context) {
    return Joiner.on("&").join(from(context.getParameters())
        .filter(onlyOneAllowableValue())
        .transform(queryStringWithValue())
        .toSet())
        .trim();
  }

  private Predicate<Parameter> onlyOneAllowableValue() {
    return new Predicate<Parameter>() {
      @Override
      public boolean apply(Parameter input) {
        AllowableValues allowableValues = input.getAllowableValues();
        return allowableValues != null
            && allowableValues instanceof AllowableListValues
            && ((AllowableListValues) allowableValues).getValues().size() == 1;
      }
    };
  }

  private Predicate<Parameter> queryStringParams() {
    return new Predicate<Parameter>() {
      @Override
      public boolean apply(Parameter input) {
        return "query".equals(input.getParamType());
      }
    };
  }

  private Function<Parameter, String> paramName() {
    return new Function<Parameter, String>() {
      @Override
      public String apply(Parameter input) {
        return input.getName();
      }
    };
  }


  private Function<Parameter, String> queryStringWithValue() {
    return new Function<Parameter, String>() {
      @Override
      public String apply(Parameter input) {
        AllowableListValues allowableValues = (AllowableListValues) input.getAllowableValues();
        return String.format("%s=%s", input.getName(), allowableValues.getValues().get(0).trim());
      }
    };
  }

  @Override
  public boolean supports(DocumentationContext delimiter) {
    return delimiter.isUriTemplatesEnabled();
  }
}
