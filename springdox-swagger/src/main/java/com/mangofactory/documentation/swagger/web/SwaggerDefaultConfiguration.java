package com.mangofactory.documentation.swagger.web;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.documentation.schema.WildcardType;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.DefaultsProviderPlugin;
import com.mangofactory.documentation.spi.service.contexts.Defaults;
import com.mangofactory.documentation.spi.service.contexts.DocumentationContextBuilder;
import com.mangofactory.documentation.spring.web.plugins.DefaultConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.documentation.schema.AlternateTypeRules.*;

@Component
public class SwaggerDefaultConfiguration implements DefaultsProviderPlugin {

  private final DefaultConfiguration defaultConfiguration;

  @Autowired
  public SwaggerDefaultConfiguration(Defaults defaults, TypeResolver typeResolver, ServletContext servletContext) {
    defaultConfiguration = new DefaultConfiguration(defaults, typeResolver, servletContext);
  }

  @Override
  public DocumentationContextBuilder create(DocumentationType documentationType) {
    return defaultConfiguration.create(documentationType)
            .rules(newArrayList(newMapRule(WildcardType.class, WildcardType.class)));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return DocumentationType.SWAGGER_12.equals(delimiter);
  }
}
