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

package springfox.documentation.swagger.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan(basePackages = {
        "springfox.documentation.swagger.schema",
        "springfox.documentation.swagger.readers",
        "springfox.documentation.swagger.web"
})
public class SwaggerCommonConfiguration {

/*   TODO - this spits out an ominous warning
 2015-04-01 22:52:09.134  WARN 26137 --- [           main] o.s.c.a.ConfigurationClassEnhancer       : @Bean method
 SwaggerCommonConfiguration.swaggerProperties is non-static and returns an object assignable to Spring's
 BeanFactoryPostProcessor interface. This will result in a failure to process annotations such as @Autowired, @Resource and
 @PostConstruct within the method's declaring @Conf
*/
  @Bean
  public PropertySourcesPlaceholderConfigurer swaggerProperties() {
    return new PropertySourcesPlaceholderConfigurer();
  }
}
