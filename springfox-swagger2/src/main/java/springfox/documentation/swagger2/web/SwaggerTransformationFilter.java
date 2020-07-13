package springfox.documentation.swagger2.web;

import io.swagger.models.Swagger;
import org.springframework.plugin.core.Plugin;
import springfox.documentation.spi.DocumentationType;

public interface SwaggerTransformationFilter<T> extends Plugin<DocumentationType> {
  Swagger transform(SwaggerTransformationContext<T> context);
}
