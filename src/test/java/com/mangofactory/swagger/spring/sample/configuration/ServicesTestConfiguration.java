package com.mangofactory.swagger.spring.sample.configuration;

import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.configuration.DefaultConfigurationModule;
import com.mangofactory.swagger.configuration.DocumentationConfig;
import com.mangofactory.swagger.configuration.ExtensibilityModule;
import com.mangofactory.swagger.models.AlternateTypeProcessingRule;
import org.joda.time.LocalDate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Date;

import static com.mangofactory.swagger.models.IgnorableTypeRule.ignorable;

@Configuration
@EnableWebMvc
@Import(DocumentationConfig.class)
@ComponentScan("com.mangofactory.swagger.spring.sample")
public class ServicesTestConfiguration {
    @Bean
    public SwaggerConfiguration swaggerConfiguration(DefaultConfigurationModule defaultConfig,
                                                     ExtensibilityModule extensibility) {
        SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration("2.0", "/some-path");
        swaggerConfiguration.getExcludedResources().add("/excluded");
        swaggerConfiguration.getTypeProcessingRules().add(new AlternateTypeProcessingRule(BigDecimal.class,
                Float.class));
        swaggerConfiguration.getTypeProcessingRules().add(new AlternateTypeProcessingRule(LocalDate.class,
                Date.class));
        swaggerConfiguration.getTypeProcessingRules().add(ignorable(UriComponentsBuilder.class));
        return extensibility.apply(defaultConfig.apply(swaggerConfiguration));
    }

}
