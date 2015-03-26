package springfox.documentation.swagger.web;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.DefaultsProviderPlugin;
import springfox.documentation.spi.service.contexts.Defaults;
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder;
import springfox.documentation.spring.web.plugins.DefaultConfiguration;

import javax.servlet.ServletContext;

import static com.google.common.collect.Lists.*;
import static springfox.documentation.schema.AlternateTypeRules.*;

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
