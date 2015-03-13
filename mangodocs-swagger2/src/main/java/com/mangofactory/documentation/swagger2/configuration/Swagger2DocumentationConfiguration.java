package com.mangofactory.documentation.swagger2.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.documentation.spring.web.SpringMvcDocumentationConfiguration;
import com.mangofactory.documentation.schema.configuration.ObjectMapperConfigured;
import com.mangofactory.documentation.swagger.configuration.SwaggerCommonConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
@EnableWebMvc
@Import({ SpringMvcDocumentationConfiguration.class, SwaggerCommonConfiguration.class})
@ComponentScan(basePackages = {
        "com.mangofactory.documentation.swagger.schema",
        "com.mangofactory.documentation.swagger.readers.operation",
        "com.mangofactory.documentation.swagger.readers.parameter",
        "com.mangofactory.documentation.swagger2.web",
        "com.mangofactory.documentation.swagger2.mappers"
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
