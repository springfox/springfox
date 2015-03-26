package springfox.documentation.swagger.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.spring.web.SpringMvcDocumentationConfiguration;

@Configuration
@Import({SpringMvcDocumentationConfiguration.class, SwaggerCommonConfiguration.class, JacksonSwaggerSupport.class})
@ComponentScan(basePackages = {
        "springfox.documentation.swagger.schema",
        "springfox.documentation.swagger.web",
        "springfox.documentation.swagger.readers.operation",
        "springfox.documentation.swagger.readers.parameter",
        "springfox.documentation.swagger.mappers"
})
public class SwaggerSpringMvcDocumentationConfiguration {

}
