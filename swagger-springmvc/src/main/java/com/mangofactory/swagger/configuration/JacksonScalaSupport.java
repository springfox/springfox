package com.mangofactory.swagger.configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ResourceListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.util.List;

@SuppressWarnings({"SpringJavaAutowiringInspection"})
public class JacksonScalaSupport {

  private Boolean registerScalaModule = false;
  private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

  @PostConstruct
  public void init() {
    List<HttpMessageConverter<?>> messageConverters = requestMappingHandlerAdapter.getMessageConverters();
    for (HttpMessageConverter<?> messageConverter : messageConverters) {
      if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
        MappingJackson2HttpMessageConverter m = (MappingJackson2HttpMessageConverter) messageConverter;
        m.getObjectMapper().registerModule(swaggerSerializationModule());
      }
    }
  }

  @Autowired
  public void setRequestMappingHandlerAdapter(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
    this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
  }

  public Boolean getRegisterScalaModule() {
    return registerScalaModule;
  }

  public Module swaggerSerializationModule() {
    SimpleModule module = new SimpleModule("SwaggerJacksonModule");
    module.addSerializer(ApiListing.class, new SwaggerApiListingJsonSerializer());
    module.addSerializer(ResourceListing.class, new SwaggerResourceListingJsonSerializer());
    return module;
  }

  @Deprecated
  /**
   * @deprecated Scala module no longer used and the SwaggerJacksonModule is not optional
   */
  public void setRegisterScalaModule(Boolean registerScalaModule) {
    this.registerScalaModule = registerScalaModule;
  }
}