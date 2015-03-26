package springfox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.ApiListingContext;

public interface ApiListingBuilderPlugin extends Plugin<DocumentationType> {
  public void apply(ApiListingContext apiListingContext);
}
