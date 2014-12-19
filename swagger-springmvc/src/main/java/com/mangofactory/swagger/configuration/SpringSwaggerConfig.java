package com.mangofactory.swagger.configuration;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.annotations.ApiIgnore;
import com.mangofactory.swagger.core.ClassOrApiAnnotationResourceGrouping;
import com.mangofactory.swagger.core.ResourceGroupingStrategy;
import com.mangofactory.swagger.core.SwaggerCache;
import com.mangofactory.swagger.models.ModelProvider;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration;
import com.mangofactory.swagger.models.dto.ResponseMessage;
import com.mangofactory.swagger.models.dto.builder.ResponseMessageBuilder;
import com.mangofactory.swagger.paths.RelativeSwaggerPathProvider;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.plugin.SwaggerPluginAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Configuration
@ComponentScan(basePackages = {"com.mangofactory.swagger.controllers"})
@Import(SwaggerModelsConfiguration.class)
public class SpringSwaggerConfig {

  @Autowired
  private List<RequestMappingHandlerMapping> handlerMappings;

  @Autowired
  private ServletContext servletContext;

  @Autowired
  private ModelProvider modelProvider;

  @Autowired
  private AlternateTypeProvider alternateTypeProvider;

  @Autowired
  private TypeResolver typeResolver;


  @Bean
  public List<RequestMappingHandlerMapping> swaggerRequestMappingHandlerMappings() {
    return handlerMappings;
  }

  @Bean
  public ResourceGroupingStrategy defaultResourceGroupingStrategy() {
    return new ClassOrApiAnnotationResourceGrouping();
  }

  @Bean
  public List<Class<? extends Annotation>> defaultExcludeAnnotations() {
    List<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>();
    annotations.add(ApiIgnore.class);
    return annotations;
  }

  @Bean
  public SwaggerPathProvider defaultSwaggerPathProvider() {
    return new RelativeSwaggerPathProvider(servletContext);
  }

  @Bean
  public SwaggerCache swaggerCache() {
    return new SwaggerCache();
  }

  @Bean
  public Set<Class> defaultIgnorableParameterTypes() {
    HashSet<Class> ignored = newHashSet();
    ignored.add(ServletRequest.class);
    ignored.add(HttpHeaders.class);
    ignored.add(ServletResponse.class);
    ignored.add(HttpServletRequest.class);
    ignored.add(HttpServletResponse.class);
    ignored.add(HttpHeaders.class);
    ignored.add(BindingResult.class);
    ignored.add(ServletContext.class);
    ignored.add(UriComponentsBuilder.class);
    ignored.add(ApiIgnore.class);
    return ignored;
  }

  public AlternateTypeProvider defaultAlternateTypeProvider() {
    return alternateTypeProvider;
  }

  /**
   * Default response messages set on all api operations
   */
  @Bean
  public Map<RequestMethod, List<ResponseMessage>> defaultResponseMessages() {
    LinkedHashMap<RequestMethod, List<ResponseMessage>> responses = newLinkedHashMap();
    responses.put(GET, asList(
            new ResponseMessageBuilder()
                    .code(OK.value())
                    .message(OK.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(NOT_FOUND.value())
                    .message(NOT_FOUND.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(FORBIDDEN.value())
                    .message(FORBIDDEN.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(UNAUTHORIZED.value())
                    .message(UNAUTHORIZED.getReasonPhrase())
                    .responseModel(null).build()));

    responses.put(PUT, asList(
            new ResponseMessageBuilder()
                    .code(CREATED.value())
                    .message(CREATED.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(NOT_FOUND.value())
                    .message(NOT_FOUND.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(FORBIDDEN.value())
                    .message(FORBIDDEN.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(UNAUTHORIZED.value())
                    .message(UNAUTHORIZED.getReasonPhrase())
                    .responseModel(null).build()));

    responses.put(POST, asList(
            new ResponseMessageBuilder()
                    .code(CREATED.value())
                    .message(CREATED.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(NOT_FOUND.value())
                    .message(NOT_FOUND.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(FORBIDDEN.value())
                    .message(FORBIDDEN.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(UNAUTHORIZED.value())
                    .message(UNAUTHORIZED.getReasonPhrase())
                    .responseModel(null).build()));

    responses.put(DELETE, asList(
            new ResponseMessageBuilder()
                    .code(NO_CONTENT.value())
                    .message(NO_CONTENT.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(FORBIDDEN.value())
                    .message(FORBIDDEN.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(UNAUTHORIZED.value())
                    .message(UNAUTHORIZED.getReasonPhrase())
                    .responseModel(null)
                    .build()));

    responses.put(PATCH, asList(
            new ResponseMessageBuilder()
                    .code(NO_CONTENT.value())
                    .message(NO_CONTENT.getReasonPhrase())
                    .responseModel(null).build(),
            new ResponseMessageBuilder()
                    .code(FORBIDDEN.value())
                    .message(FORBIDDEN.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(UNAUTHORIZED.value())
                    .message(UNAUTHORIZED.getReasonPhrase())
                    .responseModel(null)
                    .build()));

    responses.put(TRACE, asList(
            new ResponseMessageBuilder()
                    .code(NO_CONTENT.value())
                    .message(NO_CONTENT.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(FORBIDDEN.value())
                    .message(FORBIDDEN.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(UNAUTHORIZED.value())
                    .message(UNAUTHORIZED.getReasonPhrase())
                    .responseModel(null)
                    .build()));

    responses.put(OPTIONS, asList(
            new ResponseMessageBuilder()
                    .code(NO_CONTENT.value())
                    .message(NO_CONTENT.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(FORBIDDEN.value())
                    .message(FORBIDDEN.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(UNAUTHORIZED.value())
                    .message(UNAUTHORIZED.getReasonPhrase())
                    .responseModel(null)
                    .build()));
    responses.put(HEAD, asList(
            new ResponseMessageBuilder()
                    .code(NO_CONTENT.value())
                    .message(NO_CONTENT.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(FORBIDDEN.value())
                    .message(FORBIDDEN.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(UNAUTHORIZED.value())
                    .message(UNAUTHORIZED.getReasonPhrase())
                    .responseModel(null)
                    .build()));
    return responses;
  }

  @Bean
  public SwaggerPluginAdapter swaggerPluginAdapter() {
    return new SwaggerPluginAdapter(this);
  }

  public ModelProvider defaultModelProvider() {
    return modelProvider;
  }

  private List<ResponseMessage> asList(ResponseMessage... responseMessages) {
    List<ResponseMessage> list = new ArrayList();
    for (ResponseMessage responseMessage : responseMessages) {
      list.add(responseMessage);
    }
    return list;
  }

  /**
   * Registers some custom serializers needed to transform swagger models to swagger-ui required json format.
   */
  @Bean
  public JacksonSwaggerSupport jacksonSwaggerSupport() {
    return new JacksonSwaggerSupport();
  }

}
