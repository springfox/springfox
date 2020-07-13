package springfox.documentation.oas.web;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.plugin.core.Plugin;
import springfox.documentation.spi.DocumentationType;

public interface OpenApiTransformationFilter<T> extends Plugin<DocumentationType> {
  OpenAPI transform(OpenApiTransformationContext<T> context);
}
