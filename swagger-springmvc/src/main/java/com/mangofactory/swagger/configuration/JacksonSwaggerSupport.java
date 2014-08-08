package com.mangofactory.swagger.configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ResourceListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.util.List;


public class JacksonSwaggerSupport {
  private ObjectMapper springsMessageConverterObjectMapper;
  private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

  public ObjectMapper getSpringsMessageConverterObjectMapper() {
    return springsMessageConverterObjectMapper;
  }

  private Module swaggerSerializationModule() {
    SimpleModule module = new SimpleModule("SwaggerJacksonModule");
    module.addSerializer(ApiListing.class, new SwaggerApiListingJsonSerializer());
    module.addSerializer(ResourceListing.class, new SwaggerResourceListingJsonSerializer());
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
      }
    }
  }

}