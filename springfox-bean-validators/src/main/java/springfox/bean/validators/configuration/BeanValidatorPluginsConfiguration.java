/*
 *
 *  Copyright 2015-2017 the original author or authors.
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
package springfox.bean.validators.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.bean.validators.plugins.parameter.ExpandedParameterMinMaxAnnotationPlugin;
import springfox.bean.validators.plugins.parameter.ExpandedParameterNotBlankAnnotationPlugin;
import springfox.bean.validators.plugins.parameter.ExpandedParameterNotNullAnnotationPlugin;
import springfox.bean.validators.plugins.parameter.ExpandedParameterPatternAnnotationPlugin;
import springfox.bean.validators.plugins.parameter.ExpandedParameterSizeAnnotationPlugin;
import springfox.bean.validators.plugins.schema.DecimalMinMaxAnnotationPlugin;
import springfox.bean.validators.plugins.schema.MinMaxAnnotationPlugin;
import springfox.bean.validators.plugins.schema.NotBlankAnnotationPlugin;
import springfox.bean.validators.plugins.schema.NotNullAnnotationPlugin;
import springfox.bean.validators.plugins.schema.PatternAnnotationPlugin;
import springfox.bean.validators.plugins.schema.SizeAnnotationPlugin;

@Configuration
public class BeanValidatorPluginsConfiguration {

  @Bean
  public ExpandedParameterMinMaxAnnotationPlugin expanderMinMax() {
    return new ExpandedParameterMinMaxAnnotationPlugin();
  }

  @Bean
  public ExpandedParameterNotNullAnnotationPlugin expanderNotNull() {
    return new ExpandedParameterNotNullAnnotationPlugin();
  }

  @Bean
  public ExpandedParameterNotBlankAnnotationPlugin expanderNotBlank() {
    return new ExpandedParameterNotBlankAnnotationPlugin();
  }

  @Bean
  public ExpandedParameterPatternAnnotationPlugin expanderPattern() {
    return new ExpandedParameterPatternAnnotationPlugin();
  }

  @Bean
  public ExpandedParameterSizeAnnotationPlugin expanderSize() {
    return new ExpandedParameterSizeAnnotationPlugin();
  }

  @Bean
  public springfox.bean.validators.plugins.parameter.MinMaxAnnotationPlugin parameterMinMax() {
    return new springfox.bean.validators.plugins.parameter.MinMaxAnnotationPlugin();
  }

  @Bean
  public springfox.bean.validators.plugins.parameter.NotNullAnnotationPlugin parameterNotNull() {
    return new springfox.bean.validators.plugins.parameter.NotNullAnnotationPlugin();
  }

  @Bean
  public springfox.bean.validators.plugins.parameter.NotBlankAnnotationPlugin parameterNotBlank() {
    return new springfox.bean.validators.plugins.parameter.NotBlankAnnotationPlugin();
  }

  @Bean
  public springfox.bean.validators.plugins.parameter.PatternAnnotationPlugin parameterPattern() {
    return new springfox.bean.validators.plugins.parameter.PatternAnnotationPlugin();
  }

  @Bean
  public springfox.bean.validators.plugins.parameter.SizeAnnotationPlugin parameterSize() {
    return new springfox.bean.validators.plugins.parameter.SizeAnnotationPlugin();
  }

  @Bean
  public MinMaxAnnotationPlugin minMaxPlugin() {
    return new MinMaxAnnotationPlugin();
  }

  @Bean
  public DecimalMinMaxAnnotationPlugin decimalMinMaxPlugin() {
    return new DecimalMinMaxAnnotationPlugin();
  }

  @Bean
  public SizeAnnotationPlugin sizePlugin() {
    return new SizeAnnotationPlugin();
  }

  @Bean
  public NotNullAnnotationPlugin notNullPlugin() {
    return new NotNullAnnotationPlugin();
  }

  @Bean
  public NotBlankAnnotationPlugin notBlankPlugin() {
    return new NotBlankAnnotationPlugin();
  }

  @Bean
  public PatternAnnotationPlugin patternPlugin() {
    return new PatternAnnotationPlugin();
  }
}
