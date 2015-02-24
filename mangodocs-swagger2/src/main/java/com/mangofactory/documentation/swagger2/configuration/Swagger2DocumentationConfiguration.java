package com.mangofactory.documentation.swagger2.configuration;

import com.mangofactory.documentation.spring.web.SpringMvcDocumentationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ SpringMvcDocumentationConfiguration.class })
@ComponentScan(basePackages = {
        "com.mangofactory.documentation.swagger.schema",
        "com.mangofactory.documentation.swagger.web",
        "com.mangofactory.documentation.swagger.readers.operation",
        "com.mangofactory.documentation.swagger.readers.parameter",
        "com.mangofactory.documentation.swagger2.web",
        "com.mangofactory.documentation.swagger2.mappers"
})
public class Swagger2DocumentationConfiguration {
}
