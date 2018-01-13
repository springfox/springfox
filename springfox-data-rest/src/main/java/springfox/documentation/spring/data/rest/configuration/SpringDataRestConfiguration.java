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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import springfox.documentation.annotations.Incubating;
import springfox.documentation.builders.AlternateTypeBuilder;
import springfox.documentation.builders.AlternateTypePropertyBuilder;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;

import java.lang.reflect.Type;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static springfox.documentation.schema.AlternateTypeRules.*;

@Configuration
@ComponentScan(basePackages = "springfox.documentation.spring.data.rest")
@Incubating("2.5.0")
public class SpringDataRestConfiguration {

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
        return newArrayList(
            newRule(resolver.resolve(Pageable.class), resolver.resolve(pageableMixin(restConfiguration)))
        );
      }
    };
  }
  // tag::alternate-type-rule-convention[]

  // tag::alternate-type-builder[]
  private Type pageableMixin(RepositoryRestConfiguration restConfiguration) {
    return new AlternateTypeBuilder()
        .fullyQualifiedClassName(
            String.format("%s.generated.%s",
                Pageable.class.getPackage().getName(),
                Pageable.class.getSimpleName()))
        .withProperties(newArrayList(
            property(Integer.class, restConfiguration.getPageParamName()),
            property(Integer.class, restConfiguration.getLimitParamName()),
            property(String.class, restConfiguration.getSortParamName())
        ))
        .build();
  }

  private AlternateTypePropertyBuilder property(Class<?> type, String name) {
    return new AlternateTypePropertyBuilder()
        .withName(name)
        .withType(type)
        .withCanRead(true)
        .withCanWrite(true);
  }
  // tag::alternate-type-builder[]
}
