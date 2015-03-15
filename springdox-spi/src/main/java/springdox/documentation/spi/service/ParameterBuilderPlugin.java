package springdox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.contexts.ParameterContext;

public interface ParameterBuilderPlugin extends Plugin<DocumentationType> {
  public void apply(ParameterContext parameterContext);
}
