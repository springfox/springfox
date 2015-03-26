package springfox.documentation.swagger.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
   "springfox.documentation.swagger.schema",
   "springfox.documentation.swagger.readers",
   "springfox.documentation.swagger.web"
})
public class SwaggerCommonConfiguration {
}
