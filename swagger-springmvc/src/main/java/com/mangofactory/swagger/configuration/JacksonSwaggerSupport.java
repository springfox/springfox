package com.mangofactory.swagger.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mangofactory.swagger.models.property.provider.DefaultModelPropertiesProvider;
import com.wordnik.swagger.models.Model;
import com.wordnik.swagger.models.properties.Property;
import com.wordnik.swagger.util.ModelDeserializer;
import com.wordnik.swagger.util.PropertyDeserializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;


public class JacksonSwaggerSupport implements ApplicationContextAware {
  private ObjectMapper springsMessageConverterObjectMapper;
  private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
  private ApplicationContext applicationContext;

  public ObjectMapper getSpringsMessageConverterObjectMapper() {
    return springsMessageConverterObjectMapper;
  }

  private Module swaggerSerializationModule() {
    SimpleModule module = new SimpleModule("SwaggerJacksonModule");
    module.addDeserializer(Property.class, new PropertyDeserializer());
    module.addDeserializer(Model.class, new ModelDeserializer());
    return module;
  }

  @Autowired
  public void setRequestMappingHandlerAdapter(RequestMappingHandlerAdapter[] requestMappingHandlerAdapters) {
    if (requestMappingHandlerAdapters.length > 1) {
      for (RequestMappingHandlerAdapter adapter : requestMappingHandlerAdapters) {
        if (adapter.getClass().getCanonicalName().equals(RequestMappingHandlerAdapter.class.getCanonicalName())) {
          this.requestMappingHandlerAdapter = adapter;
        }
      }
    } else {
      requestMappingHandlerAdapter = requestMappingHandlerAdapters[0];
    }
  }

  @PostConstruct
  public void setup() {
    List<HttpMessageConverter<?>> messageConverters = requestMappingHandlerAdapter.getMessageConverters();
    for (HttpMessageConverter<?> messageConverter : messageConverters) {
      if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
        MappingJackson2HttpMessageConverter m = (MappingJackson2HttpMessageConverter) messageConverter;
        this.springsMessageConverterObjectMapper = m.getObjectMapper();
        this.springsMessageConverterObjectMapper.registerModule(swaggerSerializationModule());

        //This is done by com.wordnik.swagger.util.Json may not be a good idea to interfere with springs object mapper
        this.springsMessageConverterObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.springsMessageConverterObjectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.springsMessageConverterObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      }
    }

    Map<String, DefaultModelPropertiesProvider> beans =
            applicationContext.getBeansOfType(DefaultModelPropertiesProvider.class);

    for (DefaultModelPropertiesProvider defaultModelPropertiesProvider : beans.values()) {
      defaultModelPropertiesProvider.setObjectMapper(this.springsMessageConverterObjectMapper);
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}