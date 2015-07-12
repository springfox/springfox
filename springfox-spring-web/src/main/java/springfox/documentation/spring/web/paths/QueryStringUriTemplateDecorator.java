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
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import springfox.documentation.service.PathDecorator;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;

import java.util.Set;

import static com.google.common.collect.FluentIterable.*;

@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE + 10)
public class QueryStringUriTemplateDecorator implements PathDecorator {
  @Override
  public Function<String, String> decorator(final RequestMappingContext context) {
    return new Function<String, String>() {
      @Override
      public String apply(String input) {
        Set<String> expressions = positiveQueryParamNames(context);
        String prefix = requiresContinuation(input) ? "{&" : "{?";
        String queryTemplate = Joiner.on(',').join(expressions);
        if (queryTemplate.length() == 0) {
          return input;
        }
        return String.format("%s%s%s}", input, prefix, queryTemplate);
      }
    };
  }

  private boolean requiresContinuation(String url) {
    return url.contains("?");
  }

  private Set<String> positiveQueryParamNames(RequestMappingContext context) {
    return from(context.getRequestMappingInfo().getParamsCondition().getExpressions())
        .filter(positiveExpressionsOnly())
        .transform(paramName())
        .toSet();
  }

  private Function<? super NameValueExpression<String>, String> paramName() {
    return new Function<NameValueExpression<String>, String>() {
      @Override
      public String apply(NameValueExpression<String> input) {
        return input.getName();
      }
    };
  }

  private Predicate<? super NameValueExpression<String>> positiveExpressionsOnly() {
    return new Predicate<NameValueExpression<String>>() {
      @Override
      public boolean apply(NameValueExpression<String> input) {
        return !input.isNegated();
      }
    };
  }

  @Override
  public boolean supports(DocumentationContext delimiter) {
    return delimiter.isUriTemplatesEnabled();
  }
}
