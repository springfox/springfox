package com.mangofactory.swagger.configuration;

import com.fasterxml.jackson.module.scala.DefaultScalaModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@SuppressWarnings({"SpringJavaAutowiringInspection"})
public class JacksonScalaSupport {
   private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

   @PostConstruct
   public void init() {
      List<HttpMessageConverter<?>> messageConverters = requestMappingHandlerAdapter.getMessageConverters();
      for (HttpMessageConverter<?> messageConverter : messageConverters) {
         if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
            MappingJackson2HttpMessageConverter m = (MappingJackson2HttpMessageConverter) messageConverter;
            m.getObjectMapper().registerModule(new DefaultScalaModule());
         }
      }
   }

   @Autowired
   public void setRequestMappingHandlerAdapter(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
      this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
   }
}