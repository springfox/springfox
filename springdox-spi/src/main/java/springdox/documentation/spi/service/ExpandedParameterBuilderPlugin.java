package springdox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.contexts.ParameterExpansionContext;

public interface ExpandedParameterBuilderPlugin extends Plugin<DocumentationType> {
  public void apply(ParameterExpansionContext context);
}
