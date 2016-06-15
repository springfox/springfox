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
package springfox.bean.validators.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.bean.validators.plugins.ModelPropertyMinMaxAnnotationPlugin;
import springfox.bean.validators.plugins.ModelPropertyNotNullAnnotationPlugin;
import springfox.bean.validators.plugins.ModelPropertySizeAnnotationPlugin;

@Configuration
public class BeanValidatorPluginsConfiguration {
    
    /**
     * define MinMax-Plugin
     * @return
     */
  @Bean
  public ModelPropertyMinMaxAnnotationPlugin minMaxPlugin() {
    return new ModelPropertyMinMaxAnnotationPlugin();
  }

  /**
     * define Size-Plugin
     * @return
     */
  @Bean
  public ModelPropertySizeAnnotationPlugin sizePlugin() {
    return new ModelPropertySizeAnnotationPlugin();
  }

  /**
   * define NotNull-Plugin
   * @return
   */
  @Bean
  public ModelPropertyNotNullAnnotationPlugin notNullPlugin() {
    return new ModelPropertyNotNullAnnotationPlugin();
  }
}
