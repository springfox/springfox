package com.mangofactory.swagger.configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ResourceListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class JacksonSwaggerSupport {

  private ObjectMapper springsMessageConverterObjectMapper;

  @Autowired
  @Qualifier(value = "springsMessageConverterObjectMapper")
  public void setSpringsMessageConverterObjectMapper(ObjectMapper springsMessageConverterObjectMapper) {
    springsMessageConverterObjectMapper.registerModule(swaggerSerializationModule());
    this.springsMessageConverterObjectMapper = springsMessageConverterObjectMapper;
  }

  public ObjectMapper getSpringsMessageConverterObjectMapper() {
    return springsMessageConverterObjectMapper;
  }

  private Module swaggerSerializationModule() {
    SimpleModule module = new SimpleModule("SwaggerJacksonModule");
    module.addSerializer(ApiListing.class, new SwaggerApiListingJsonSerializer());
    module.addSerializer(ResourceListing.class, new SwaggerResourceListingJsonSerializer());
    return module;
  }
}