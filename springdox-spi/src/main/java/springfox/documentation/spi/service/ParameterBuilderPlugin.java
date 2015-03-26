package springfox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.ParameterContext;

public interface ParameterBuilderPlugin extends Plugin<DocumentationType> {
  public void apply(ParameterContext parameterContext);
}
