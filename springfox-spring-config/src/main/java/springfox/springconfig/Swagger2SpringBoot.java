package springfox.springconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.petstore.controller.PetController;

@SpringBootApplication
@EnableSwagger2   //<1>
@ComponentScan(basePackageClasses = {PetController.class})    //<2>
public class Swagger2SpringBoot {

  public static void main(String[] args) {
    ApplicationContext ctx = SpringApplication.run(Swagger2SpringBoot.class, args);
  }

  @Bean
  public Docket petApi() {
    return new Docket(DocumentationType.SWAGGER_2)    //<3>
            .select()   //<4>
            .build();   //<5>
  }
}
