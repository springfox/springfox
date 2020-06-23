package springfox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.ResponseContext;

public interface ResponseBuilderPlugin extends Plugin<DocumentationType> {
  /**
   * Implement this method to enrich return values
   *
   * @param responseContext - context that can be used to override the parameter attributes
   * @see springfox.documentation.service.Response
   * @see springfox.documentation.builders.ResponseBuilder
   */
  void apply(ResponseContext responseContext);
}
