package springfox.test.contract.swagger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class SwaggerApplication {
  public static void main(String[] args) {
    SpringApplication.run(SwaggerApplication.class, args);
  }
}
