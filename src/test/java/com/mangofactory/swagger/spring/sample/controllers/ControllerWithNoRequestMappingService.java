package com.mangofactory.swagger.spring.sample.controllers;

import com.mangofactory.swagger.spring.sample.EnumType;
import com.mangofactory.swagger.spring.sample.Example;
import com.mangofactory.swagger.spring.sample.NestedType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class ControllerWithNoRequestMappingService {
    @RequestMapping(value = "/no-request-mapping", method = RequestMethod.GET)
    public ResponseEntity<Example> exampleWithNoRequestMapping(UriComponentsBuilder builder) {
        return new ResponseEntity<Example>(new Example("Hello", 1, EnumType.ONE, new NestedType("test")), HttpStatus.OK);
    }
}
