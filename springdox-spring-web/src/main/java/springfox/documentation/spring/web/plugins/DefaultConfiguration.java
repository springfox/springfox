package springfox.documentation.spring.web.plugins;

import com.fasterxml.classmate.TypeResolver;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.DefaultsProviderPlugin;
import springfox.documentation.spi.service.contexts.ApiSelector;
import springfox.documentation.spi.service.contexts.Defaults;
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder;
import springfox.documentation.spring.web.RelativePathProvider;

import javax.servlet.ServletContext;

public class DefaultConfiguration implements DefaultsProviderPlugin {

  private final Defaults defaults;
  private final TypeResolver typeResolver;
  private final ServletContext servletContext;

  public DefaultConfiguration(Defaults defaults,
                       TypeResolver typeResolver,
                       ServletContext servletContext) {

    this.servletContext = servletContext;
    this.defaults = defaults;
    this.typeResolver = typeResolver;
  }

  @Override
  public DocumentationContextBuilder create(DocumentationType documentationType) {
    return new DocumentationContextBuilder(documentationType)
            .operationOrdering(defaults.operationOrdering())
            .apiDescriptionOrdering(defaults.apiDescriptionOrdering())
            .apiListingReferenceOrdering(defaults.apiListingReferenceOrdering())
            .additionalIgnorableTypes(defaults.defaultIgnorableParameterTypes())
            .rules(defaults.defaultRules(typeResolver))
            .defaultResponseMessages(defaults.defaultResponseMessages())
            .pathProvider(new RelativePathProvider(servletContext))
            .typeResolver(typeResolver)
            .selector(ApiSelector.DEFAULT);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
