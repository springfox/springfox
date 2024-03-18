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

package springfox.documentation.spring.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.*;

public class WebMvcObjectMapperConfigurer implements BeanPostProcessor, ApplicationEventPublisherAware {

  private ApplicationEventPublisher applicationEventPublisher;

  @Override
  public Object postProcessBeforeInitialization(
      Object bean,
      String beanName) throws BeansException {

    if (bean instanceof RequestMappingHandlerAdapter) {
      RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;
      adapter.setMessageConverters(configureMessageConverters(adapter.getMessageConverters()));
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(
      Object bean,
      String beanName) throws BeansException {
    return bean;
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  private List<HttpMessageConverter<?>> configureMessageConverters(
      List<HttpMessageConverter<?>> converters) {

    Iterable<MappingJackson2HttpMessageConverter> jackson2Converters = jackson2Converters(converters);
    if (StreamSupport.stream(jackson2Converters.spliterator(), false).count() > 0) {
      for (MappingJackson2HttpMessageConverter each : jackson2Converters) {
        fireObjectMapperConfiguredEvent(each.getObjectMapper());
      }
    } else {
      converters.add(configuredMessageConverter());
    }
    return new ArrayList<>(converters);
  }

  private Iterable<MappingJackson2HttpMessageConverter> jackson2Converters(
      List<HttpMessageConverter<?>> messageConverters) {

    List<MappingJackson2HttpMessageConverter> converters = messageConverters.stream()
        .filter(MappingJackson2HttpMessageConverter.class::isInstance)
        .map(MappingJackson2HttpMessageConverter.class::cast)
        .collect(toList());
    Collections.reverse(converters);
    return converters;
  }

  private MappingJackson2HttpMessageConverter configuredMessageConverter() {
    MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
    ObjectMapper objectMapper = new ObjectMapper();
    messageConverter.setObjectMapper(objectMapper);
    fireObjectMapperConfiguredEvent(objectMapper);
    return messageConverter;
  }

  private void fireObjectMapperConfiguredEvent(ObjectMapper objectMapper) {
    applicationEventPublisher.publishEvent(new ObjectMapperConfigured(this, objectMapper));
  }
}
