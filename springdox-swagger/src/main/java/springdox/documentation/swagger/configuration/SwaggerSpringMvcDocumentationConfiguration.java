package springdox.documentation.swagger.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springdox.documentation.spring.web.SpringMvcDocumentationConfiguration;

@Configuration
@Import({SpringMvcDocumentationConfiguration.class, SwaggerCommonConfiguration.class, JacksonSwaggerSupport.class})
@ComponentScan(basePackages = {
        "springdox.documentation.swagger.schema",
        "springdox.documentation.swagger.web",
        "springdox.documentation.swagger.readers.operation",
        "springdox.documentation.swagger.readers.parameter",
        "springdox.documentation.swagger.mappers"
})
public class SwaggerSpringMvcDocumentationConfiguration {

}
