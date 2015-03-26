package springfox.documentation.swagger.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger.annotations.EnableSwagger;


@Configuration
@EnableWebMvc
@EnableSwagger
@ComponentScan("springfox.documentation.spring.web.dummy")
public class DefaultJavaPluginConfig {

}
