package springdox.documentation.swagger.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springdox.documentation.swagger.annotations.EnableSwagger;

@Configuration
@EnableSwagger
@ComponentScan("springdox.documentation.spring.web.dummy")
public class MultipleRMHAConfig {


}
