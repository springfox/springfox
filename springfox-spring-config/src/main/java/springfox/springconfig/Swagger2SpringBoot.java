package springfox.springconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.petstore.controller.PetController;

@SpringBootApplication
@EnableSwagger2 //1
@ComponentScan(basePackageClasses = {PetController.class})
public class Swagger2SpringBoot {
  public static void main(String[] args) {
    ApplicationContext ctx = SpringApplication.run(Swagger2SpringBoot.class, args);
  }
}
