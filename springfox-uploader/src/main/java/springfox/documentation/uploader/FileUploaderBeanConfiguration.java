package springfox.documentation.uploader;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;

@Configuration
@Import({Swagger2DocumentationConfiguration.class})
@ComponentScan(basePackages = {"springfox.documentation.uploader"})
public class FileUploaderBeanConfiguration {
}
