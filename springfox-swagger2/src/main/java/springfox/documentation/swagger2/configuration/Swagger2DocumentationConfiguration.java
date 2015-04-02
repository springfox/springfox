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

package springfox.documentation.swagger2.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;
import springfox.documentation.spring.web.SpringMvcDocumentationConfiguration;
import springfox.documentation.swagger.configuration.SwaggerCommonConfiguration;

import java.util.List;

@Configuration
@Import({ SpringMvcDocumentationConfiguration.class, SwaggerCommonConfiguration.class})
@ComponentScan(basePackages = {
        "springfox.documentation.swagger.schema",
        "springfox.documentation.swagger.readers.operation",
        "springfox.documentation.swagger.readers.parameter",
        "springfox.documentation.swagger2.web",
        "springfox.documentation.swagger2.mappers"
})
public class Swagger2DocumentationConfiguration extends WebMvcConfigurerAdapter
        implements ApplicationEventPublisherAware {
  private ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    if (converters.size() > 0) {
      for (HttpMessageConverter<?> each : converters) {
        maybeConfigureObjectMapper(each, swagger2JacksonModule());
      }
    } else {
      converters.add(configuredMessageConverter());
    }
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  private MappingJackson2HttpMessageConverter configuredMessageConverter() {
    MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
    ObjectMapper objectMapper = new ObjectMapper();
    messageConverter.setObjectMapper(objectMapper);
    maybeConfigureObjectMapper(messageConverter, swagger2JacksonModule());
    return messageConverter;
  }

  private void maybeConfigureObjectMapper(HttpMessageConverter<?> converter, Swagger2JacksonModule module) {
    if (converter instanceof MappingJackson2HttpMessageConverter) {
      ObjectMapper objectMapper = ((MappingJackson2HttpMessageConverter) converter).getObjectMapper();
      if (!Swagger2JacksonModule.isRegistered(objectMapper)) {
        objectMapper.registerModule(module);
      }
      fireObjectMapperConfiguredEvent(objectMapper);
    }
  }

  private void fireObjectMapperConfiguredEvent(ObjectMapper objectMapper) {
    applicationEventPublisher.publishEvent(new ObjectMapperConfigured(this, objectMapper));
  }

  @Bean
  Swagger2JacksonModule swagger2JacksonModule() {
    return new Swagger2JacksonModule();
  }
}
