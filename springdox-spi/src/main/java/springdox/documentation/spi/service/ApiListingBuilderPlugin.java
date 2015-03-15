package springdox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.contexts.ApiListingContext;

public interface ApiListingBuilderPlugin extends Plugin<DocumentationType> {
  public void apply(ApiListingContext apiListingContext);
}
