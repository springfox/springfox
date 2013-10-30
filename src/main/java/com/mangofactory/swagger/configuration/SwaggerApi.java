package com.mangofactory.swagger.configuration;

import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.model.ApiInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

public class SwaggerApi {
    @Getter @Setter private ApiInfo apiInfo;
    @Getter @Setter private SwaggerConfig swaggerConfig;

    @Autowired @Setter @Getter
    private WebApplicationContext webApplicationContext;

    public SwaggerApi(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
        this.swaggerConfig = new SwaggerConfig();
    }

}
