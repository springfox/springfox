package com.mangofactory.swagger.springmvc.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.mangofactory.swagger.springmvc.controller.DocumentationController;

@Configuration
@EnableWebMvc
@ComponentScan("com.mangofactory.swagger.springmvc.test")
public class TestConfiguration {

	@Bean
	public DocumentationController documentationController()
	{
		DocumentationController controller = new DocumentationController();
		controller.setApiVersion("0.2");
		controller.setSwaggerVersion("1.0");
		controller.setBasePath("http://petstore.swagger.wordnik.com/api");
		return controller;
	}
}
