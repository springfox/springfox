package com.mangofactory.swagger.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.mangofactory.swagger.models.dto.jackson.SwaggerJacksonModule;
import com.mangofactory.swagger.models.property.provider.DefaultModelPropertiesProvider;
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

import static com.google.common.collect.FluentIterable.*;


public class JacksonSwaggerSupport implements ApplicationContextAware {
  private ObjectMapper objectMapper;
  private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
  private ApplicationContext applicationContext;

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
    objectMapper = configureAllObjectMappers();
    Map<String, DefaultModelPropertiesProvider> beans =
            applicationContext.getBeansOfType(DefaultModelPropertiesProvider.class);

    for (DefaultModelPropertiesProvider defaultModelPropertiesProvider : beans.values()) {
      defaultModelPropertiesProvider.setObjectMapper(this.objectMapper);
    }
  }

  private ObjectMapper configureAllObjectMappers() {
    List<HttpMessageConverter<?>> messageConverters = requestMappingHandlerAdapter.getMessageConverters();
    ObjectMapper selected = null;
    for (HttpMessageConverter<?> messageConverter : jackson2Converters(messageConverters)) {
        MappingJackson2HttpMessageConverter m = (MappingJackson2HttpMessageConverter) messageConverter;
        selected = m.getObjectMapper();

        //Consider not using the users object mapper to serialize swagger JSON - rewrite DefaultSwaggerController
        SwaggerJacksonModule.maybeRegisterModule(selected);
    }
    ObjectMapper defaultMapper = new ObjectMapper();
    SwaggerJacksonModule.maybeRegisterModule(defaultMapper);
    return Optional.fromNullable(selected).or(defaultMapper);
  }

  private Iterable<MappingJackson2HttpMessageConverter> jackson2Converters
          (Iterable<HttpMessageConverter<?>> messageConverters) {
    return from(messageConverters).filter(MappingJackson2HttpMessageConverter.class);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}