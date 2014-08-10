//Copyright 2014 Choice Hotels International
package com.mangofactory.swagger.dummy.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.mangofactory.swagger.annotations.BeanParam;
import com.mangofactory.swagger.dummy.models.Person;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/webapi/person")
@Api(value = "Person")
public class PersonService {


    @RequestMapping(value = {"/createPerson"}, method = POST, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Creates and returns a Person",
    notes="Populates a Person instance using the data supplied in the posted form fields")
    public Person createRandomPersonWithBeanParam(

            @BeanParam
            final Person person, final BindingResult result) {

        return person;
    }

}
