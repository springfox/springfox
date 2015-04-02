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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;

import java.util.List;

import static com.google.common.collect.FluentIterable.*;
import static springfox.documentation.swagger.configuration.SwaggerJacksonModule.*;


@Configuration
public class JacksonSwaggerSupport extends WebMvcConfigurerAdapter implements ApplicationEventPublisherAware {
  private ApplicationEventPublisher applicationEventPublisher;


  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    ObjectMapper selected = null;
    for (HttpMessageConverter<?> messageConverter : jackson2Converters(converters)) {
      MappingJackson2HttpMessageConverter m = (MappingJackson2HttpMessageConverter) messageConverter;
      selected = m.getObjectMapper();

      //Consider not using the users object mapper to serialize swagger JSON - rewrite DefaultSwaggerController
      maybeRegisterModule(selected);
    }
    ObjectMapper defaultMapper = new ObjectMapper();
    maybeRegisterModule(defaultMapper);
    fireObjectMapperConfiguredEvent(Optional.fromNullable(selected).or(defaultMapper));
  }

  private Iterable<MappingJackson2HttpMessageConverter> jackson2Converters
          (Iterable<HttpMessageConverter<?>> messageConverters) {
    return from(messageConverters).filter(MappingJackson2HttpMessageConverter.class);
  }


  private void fireObjectMapperConfiguredEvent(ObjectMapper objectMapper) {
    applicationEventPublisher.publishEvent(new ObjectMapperConfigured(this, objectMapper));
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }
}