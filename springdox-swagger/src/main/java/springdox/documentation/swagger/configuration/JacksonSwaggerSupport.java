package springdox.documentation.swagger.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springdox.documentation.schema.configuration.ObjectMapperConfigured;
import springdox.documentation.swagger.jackson.SwaggerJacksonProvider;

import java.util.List;


@Configuration
@EnableWebMvc
public class JacksonSwaggerSupport extends WebMvcConfigurerAdapter implements ApplicationEventPublisherAware {
  private ApplicationEventPublisher applicationEventPublisher;


  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    if (converters.size() > 0) {
      for (HttpMessageConverter<?> each : converters) {
        maybeConfigureObjectMapper(each, new SwaggerJacksonProvider());
      }
    } else {
      converters.add(configuredMessageConverter());
    }
  }

  private HttpMessageConverter<?> configuredMessageConverter() {
    MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
    ObjectMapper objectMapper = new ObjectMapper();
    messageConverter.setObjectMapper(objectMapper);
    maybeConfigureObjectMapper(messageConverter, new SwaggerJacksonProvider());
    return messageConverter;
  }

  public void maybeConfigureObjectMapper(HttpMessageConverter<?> messageConverter,
                                         SwaggerJacksonProvider swaggerJacksonProvider) {
      if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
        MappingJackson2HttpMessageConverter m = (MappingJackson2HttpMessageConverter) messageConverter;
        ObjectMapper objectMapper = m.getObjectMapper();

        //Consider not using the users object mapper to serialize swagger JSON - rewrite DefaultSwaggerController
        objectMapper.registerModule(swaggerJacksonProvider.swaggerJacksonModule());
        fireObjectMapperConfiguredEvent(objectMapper);
      }
  }

  private void fireObjectMapperConfiguredEvent(ObjectMapper objectMapper) {
    applicationEventPublisher.publishEvent(new ObjectMapperConfigured(this, objectMapper));
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }
}