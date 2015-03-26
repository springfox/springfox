package springfox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;

public interface ExpandedParameterBuilderPlugin extends Plugin<DocumentationType> {
  public void apply(ParameterExpansionContext context);
}
