package springfox.documentation.uploader;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.spring.web.SpringfoxWebMvcConfiguration;

@Configuration
@Import({SpringfoxWebMvcConfiguration.class})
@ComponentScan(basePackages = {"springfox.documentation.uploader"})
public class FileUploaderBeanConfiguration {
}
