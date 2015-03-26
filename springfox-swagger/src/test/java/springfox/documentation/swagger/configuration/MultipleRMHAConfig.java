package springfox.documentation.swagger.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger.annotations.EnableSwagger;

@Configuration
@EnableSwagger
@ComponentScan("springfox.documentation.spring.web.dummy")
public class MultipleRMHAConfig {


}
