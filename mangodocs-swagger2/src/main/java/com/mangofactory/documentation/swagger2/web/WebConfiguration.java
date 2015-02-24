package com.mangofactory.documentation.swagger2.web;

import com.mangofactory.documentation.spring.web.SpringMvcDocumentationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ SpringMvcDocumentationConfiguration.class })
@ComponentScan(basePackages = {
        "com.mangofactory.documentation.swagger2.web",
        "com.mangofactory.documentation.swagger2.mappers"
})
public class WebConfiguration {
}

