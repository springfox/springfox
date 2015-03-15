package springdox.documentation.swagger.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
   "springdox.documentation.swagger.schema",
   "springdox.documentation.swagger.readers",
   "springdox.documentation.swagger.web"
})
public class SwaggerCommonConfiguration {
}
