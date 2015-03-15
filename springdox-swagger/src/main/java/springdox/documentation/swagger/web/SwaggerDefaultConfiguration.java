package springdox.documentation.swagger.web;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springdox.documentation.schema.WildcardType;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.DefaultsProviderPlugin;
import springdox.documentation.spi.service.contexts.Defaults;
import springdox.documentation.spi.service.contexts.DocumentationContextBuilder;
import springdox.documentation.spring.web.plugins.DefaultConfiguration;

import javax.servlet.ServletContext;

import static com.google.common.collect.Lists.*;
import static springdox.documentation.schema.AlternateTypeRules.*;

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
