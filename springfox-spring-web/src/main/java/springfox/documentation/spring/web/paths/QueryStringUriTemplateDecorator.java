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
import springfox.documentation.service.Parameter;
import springfox.documentation.service.PathDecorator;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.PathContext;

import java.util.Set;

import static com.google.common.collect.FluentIterable.*;

@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE + 60)
public class QueryStringUriTemplateDecorator implements PathDecorator {
  @Override
  public Function<String, String> decorator(final PathContext context) {
    return new Function<String, String>() {
      @Override
      public String apply(String input) {
        Set<String> expressions = queryParamNames(context);
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

  private Set<String> queryParamNames(PathContext context) {
    return from(context.getParameters())
        .filter(queryStringParams())
        .transform(paramName())
        .toSet();
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


  @Override
  public boolean supports(DocumentationContext delimiter) {
    return delimiter.isUriTemplatesEnabled();
  }
}
