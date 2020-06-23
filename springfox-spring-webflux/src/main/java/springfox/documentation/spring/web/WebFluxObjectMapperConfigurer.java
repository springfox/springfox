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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.json.Jackson2CodecSupport;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;

import java.util.List;

public class WebFluxObjectMapperConfigurer implements BeanPostProcessor, ApplicationEventPublisherAware {

  private ApplicationEventPublisher applicationEventPublisher;

  @Override
  public Object postProcessBeforeInitialization(
      Object bean,
      String beanName) throws BeansException {
    if (bean instanceof Jackson2CodecSupport) {
      Jackson2CodecSupport encoder = (Jackson2CodecSupport) bean;
      fireObjectMapperConfiguredEvent(encoder.getObjectMapper());
    }
    if (bean instanceof DefaultServerCodecConfigurer) {
      List<HttpMessageReader<?>> readers = ((DefaultServerCodecConfigurer) bean).getReaders();
      for (HttpMessageReader<?> reader : readers) {
        if (reader instanceof DecoderHttpMessageReader) {
          Decoder decoder = ((DecoderHttpMessageReader) reader).getDecoder();
          if (decoder instanceof Jackson2JsonDecoder) {
            fireObjectMapperConfiguredEvent(((Jackson2JsonDecoder) decoder).getObjectMapper());
            return bean;
          }
        }
      }
      List<HttpMessageWriter<?>> writers = ((DefaultServerCodecConfigurer) bean).getWriters();
      for (HttpMessageWriter<?> writer : writers) {
        if (writer instanceof EncoderHttpMessageWriter) {
          Encoder encoder = ((EncoderHttpMessageWriter) writer).getEncoder();
          if (encoder instanceof Jackson2JsonEncoder) {
            fireObjectMapperConfiguredEvent(((Jackson2JsonEncoder) encoder).getObjectMapper());
            return bean;
          }
        }
      }
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

  private void fireObjectMapperConfiguredEvent(ObjectMapper objectMapper) {
    applicationEventPublisher.publishEvent(new ObjectMapperConfigured(this, objectMapper));
  }
}
