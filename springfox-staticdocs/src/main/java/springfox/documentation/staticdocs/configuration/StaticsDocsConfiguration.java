package springfox.documentation.staticdocs.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;


@Configuration
@Import(Swagger2DocumentationConfiguration.class)
@ComponentScan(basePackages = {
        "springfox.documentation.swagger.web"
})
public class StaticsDocsConfiguration {

}
