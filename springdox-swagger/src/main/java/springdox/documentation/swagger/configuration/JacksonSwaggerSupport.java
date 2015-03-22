package springdox.documentation.swagger.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springdox.documentation.schema.configuration.ObjectMapperConfigured;

import java.util.List;

import static com.google.common.collect.FluentIterable.*;
import static springdox.documentation.swagger.configuration.SwaggerJacksonModule.*;


@Configuration
@EnableWebMvc
public class JacksonSwaggerSupport extends WebMvcConfigurerAdapter implements ApplicationEventPublisherAware {
  private ApplicationEventPublisher applicationEventPublisher;


  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    ObjectMapper selected = null;
    for (HttpMessageConverter<?> messageConverter : jackson2Converters(converters)) {
      MappingJackson2HttpMessageConverter m = (MappingJackson2HttpMessageConverter) messageConverter;
      selected = m.getObjectMapper();

      //Consider not using the users object mapper to serialize swagger JSON - rewrite DefaultSwaggerController
      maybeRegisterModule(selected);
    }
    ObjectMapper defaultMapper = new ObjectMapper();
    maybeRegisterModule(defaultMapper);
    fireObjectMapperConfiguredEvent(Optional.fromNullable(selected).or(defaultMapper));
  }

  private Iterable<MappingJackson2HttpMessageConverter> jackson2Converters
      (Iterable<HttpMessageConverter<?>> messageConverters) {
    return from(messageConverters).filter(MappingJackson2HttpMessageConverter.class);
  }


  private void fireObjectMapperConfiguredEvent(ObjectMapper objectMapper) {
    applicationEventPublisher.publishEvent(new ObjectMapperConfigured(this, objectMapper));
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }
}