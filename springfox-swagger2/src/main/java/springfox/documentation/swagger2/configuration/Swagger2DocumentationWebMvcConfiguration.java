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

package springfox.documentation.swagger2.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerMapping;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.WebMvcPropertySourcedRequestMappingHandlerMapping;
import springfox.documentation.spring.web.SpringfoxWebConfiguration;
import springfox.documentation.spring.web.SpringfoxWebMvcConfiguration;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.swagger.configuration.SwaggerCommonConfiguration;
import springfox.documentation.swagger2.mappers.LicenseMapper;
import springfox.documentation.swagger2.mappers.LicenseMapperImpl;
import springfox.documentation.swagger2.mappers.ModelMapper;
import springfox.documentation.swagger2.mappers.ModelMapperImpl;
import springfox.documentation.swagger2.mappers.ParameterMapper;
import springfox.documentation.swagger2.mappers.ParameterMapperImpl;
import springfox.documentation.swagger2.mappers.SecurityMapper;
import springfox.documentation.swagger2.mappers.SecurityMapperImpl;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2MapperImpl;
import springfox.documentation.swagger2.mappers.VendorExtensionsMapper;
import springfox.documentation.swagger2.mappers.VendorExtensionsMapperImpl;
import springfox.documentation.swagger2.web.Swagger2ControllerWebMvc;

@Configuration
@ConditionalOnClass(name = "springfox.documentation.spring.web.SpringfoxWebMvcConfiguration")
@Import({ SpringfoxWebConfiguration.class, SpringfoxWebMvcConfiguration.class, SwaggerCommonConfiguration.class })
public class Swagger2DocumentationWebMvcConfiguration {
  @Bean
  public JacksonModuleRegistrar swagger2Module() {
    return new Swagger2JacksonModule();
  }

  @Bean
  public HandlerMapping swagger2ControllerMapping(
      Environment environment,
      DocumentationCache documentationCache,
      ServiceModelToSwagger2Mapper mapper,
      JsonSerializer jsonSerializer) {
    return new WebMvcPropertySourcedRequestMappingHandlerMapping(
        environment,
        new Swagger2ControllerWebMvc(environment, documentationCache, mapper, jsonSerializer));
  }
  
  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapperImpl();
  }
  
  @Bean
  public ParameterMapper parameterMapper() {
    return new ParameterMapperImpl();
  }
  
  @Bean
  public SecurityMapper securityMapper() {
    return new SecurityMapperImpl();
  }
  
  @Bean
  public LicenseMapper licenseMapper() {
    return new LicenseMapperImpl();
  }
  
  @Bean
  public VendorExtensionsMapper vendorExtensionsMapper() {
    return new VendorExtensionsMapperImpl();
  }
  
  @Bean
  public ServiceModelToSwagger2Mapper serviceModelToSwagger2Mapper() {
    return new ServiceModelToSwagger2MapperImpl();
  }
}
