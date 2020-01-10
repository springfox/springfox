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

package springfox.test.contract.swagger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.petstore.PetStoreConfiguration;

@SpringBootApplication
@SuppressWarnings("HideUtilityClassConstructor")
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@ComponentScan(basePackages = {
    "springfox.test.contract.swagger",
    "springfox.petstore.controller"
})
@Import(value = {
    SpringDataRestConfiguration.class,
    PetStoreConfiguration.class,
    SecuritySupport.class,
    Swagger2TestConfig.class,
    BeanValidatorPluginsConfiguration.class })
public class SwaggerApplication {
  public static void main(String[] args) {
    SpringApplication.run(
        SwaggerApplication.class,
        args);
  }
}
