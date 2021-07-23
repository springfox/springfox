/*
 *
 *  Copyright 2016 the original author or authors.
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
package springfox.documentation.spring.data.rest.configuration;

import com.fasterxml.classmate.TypeResolver;

import io.swagger.annotations.ApiParam;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import springfox.documentation.builders.AlternateTypeBuilder;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.spring.web.OnServletBasedWebApplication;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static springfox.documentation.schema.AlternateTypeRules.*;

@Configuration
@ComponentScan(basePackages = "springfox.documentation.spring.data.rest")
@ConditionalOnWebApplication
@ConditionalOnClass({RepositoryRestConfiguration.class, OnServletBasedWebApplication.class})
public class SpringDataRestConfiguration {

  private static final String PAGE_DESCRIPTION = "Results page you want to retrieve (0..N)";

  private static final String SIZE_DESCRIPTION = "Number of records per page";

  private static final String SORT_DESCRIPTION = "Sorting criteria in the format: property(,asc|desc)." 
      + " Default sort order is ascending. Multiple sort criteria are supported.";

  // tag::alternate-type-rule-convention[]
  @Bean
  public AlternateTypeRuleConvention pageableConvention(
      final TypeResolver resolver,
      final RepositoryRestConfiguration restConfiguration) {
    return new AlternateTypeRuleConvention() {

      @Override
      public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
      }

      @Override
      public List<AlternateTypeRule> rules() {
        return Arrays.asList(
            newRule(resolver.resolve(Pageable.class), resolver.resolve(pageableMixin(restConfiguration))),
            newRule(resolver.resolve(Sort.class), resolver.resolve(sortMixin(restConfiguration)))
        );
      }
    };
  }
  // end::alternate-type-rule-convention[]

  // tag::alternate-type-builder[]
  private Type pageableMixin(RepositoryRestConfiguration restConfiguration) {
    String pageDefault = String.valueOf(0);
    String sizeDefault = String.valueOf(restConfiguration.getDefaultPageSize());
    return new AlternateTypeBuilder()
        .fullyQualifiedClassName(
            String.format("%s.generated.%s",
                Pageable.class.getPackage().getName(),
                Pageable.class.getSimpleName()))
        .property(p -> p.name(restConfiguration.getPageParamName())
            .type(Integer.class)
            .canRead(true)
            .canWrite(true)
            .annotations(Collections.singletonList(new ApiParamBuilder().value(PAGE_DESCRIPTION)
                .defaultValue(pageDefault).example(pageDefault).build())))
        .property(p -> p.name(restConfiguration.getLimitParamName())
            .type(Integer.class)
            .canRead(true)
            .canWrite(true)
            .annotations(Collections.singletonList(new ApiParamBuilder().value(SIZE_DESCRIPTION)
                .defaultValue(sizeDefault).example(sizeDefault).build())))
        .property(p -> p.name(restConfiguration.getSortParamName())
            .type(String.class)
            .canRead(true)
            .canWrite(true)
            .annotations(Collections.singletonList(new ApiParamBuilder().value(SORT_DESCRIPTION)
                .allowMultiple(true).build())))
        .build();
  }
  // end::alternate-type-builder[]
  private Type sortMixin(RepositoryRestConfiguration restConfiguration) {
    return new AlternateTypeBuilder()
      .fullyQualifiedClassName(
          String.format("%s.generated.%s",
            Sort.class.getPackage().getName(),
            Sort.class.getSimpleName()))
      .property(p -> p.name(restConfiguration.getSortParamName())
          .type(String.class)
          .canRead(true)
          .canWrite(true)
          .annotations(Collections.singletonList(new ApiParamBuilder().value(SORT_DESCRIPTION)
            .allowMultiple(true).build())))
      .build();
  }

  private static class ApiParamBuilder {
    private String value = "";
    private String defaultValue = "";
    private String example = "";
    private boolean allowMultiple = false;

    ApiParamBuilder value(String value) {
      this.value = value;
      return this;
    }

    ApiParamBuilder defaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

    ApiParamBuilder example(String example) {
      this.example = example;
      return this;
    }

    ApiParamBuilder allowMultiple(boolean allowMultiple) {
      this.allowMultiple = allowMultiple;
      return this;
    }

    ApiParam build() {
      Map<String, Object> attributes = new HashMap<>();
      attributes.put("value", value);
      attributes.put("defaultValue", defaultValue);
      attributes.put("example", example);
      attributes.put("allowMultiple", allowMultiple);
      return AnnotationUtils.synthesizeAnnotation(attributes, ApiParam.class, null);
    }
  }
}
