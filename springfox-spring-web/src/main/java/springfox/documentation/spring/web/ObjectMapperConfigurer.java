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

package springfox.documentation.spring.web;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;

import springfox.documentation.schema.configuration.ObjectMapperConfigured;

public class ObjectMapperConfigurer implements BeanPostProcessor, ApplicationEventPublisherAware {

  private ApplicationEventPublisher applicationEventPublisher;

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

    if (bean instanceof RequestMappingHandlerAdapter) {
      RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;
      adapter.setMessageConverters(configureMessageConverters(adapter.getMessageConverters()));
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  private List<HttpMessageConverter<?>> configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    List<MappingJackson2HttpMessageConverter> jackson2Converters = jackson2Converters(converters);
    if (jackson2Converters.size() > 0) {
      for (MappingJackson2HttpMessageConverter each : jackson2Converters) {
        fireObjectMapperConfiguredEvent(each.getObjectMapper());
      }
    } else {
      converters.add(configuredMessageConverter());
    }
    return new ArrayList<>(converters);
  }

  private List<MappingJackson2HttpMessageConverter> jackson2Converters(List<HttpMessageConverter<?>> messageConverters) {
    List<MappingJackson2HttpMessageConverter> j2c = new LinkedList<>();
    for (HttpMessageConverter<?> c : messageConverters) {
      if (c instanceof MappingJackson2HttpMessageConverter) {
        j2c.add(0, (MappingJackson2HttpMessageConverter) c);
      }
    }
    return j2c;
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
