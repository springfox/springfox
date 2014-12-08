package com.mangofactory.swagger.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.swagger.models.property.provider.DefaultModelPropertiesProvider;
import com.mangofactory.swagger.models.dto.jackson.SwaggerJacksonProvider;
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

//  private Module swaggerSerializationModule(ObjectMapper objectMapper) {
//    InternalObjectMapperProvider internalObjectMapperProvider = new InternalObjectMapperProvider();
//    return internalObjectMapperProvider.swaggerJacksonModule(objectMapper);
//  }


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

        //Consider not using the users object mapper to serialize swagger JSON - rewrite DefaultSwaggerController
        SwaggerJacksonProvider swaggerJacksonProvider = new SwaggerJacksonProvider();
        this.springsMessageConverterObjectMapper.registerModule(swaggerJacksonProvider.swaggerJacksonModule());
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