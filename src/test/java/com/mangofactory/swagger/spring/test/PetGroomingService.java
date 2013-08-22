package com.mangofactory.swagger.spring.test;

import com.wordnik.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
//Demonstrates multiple request mappings at the controller level
@RequestMapping({"/petgrooming", "/pets/grooming", "/pets"})
@Api(value="", description="Grooming operations for pets")
public class PetGroomingService {

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Boolean> canGroom(@RequestParam String type) {
        return new ResponseEntity<Boolean>(HttpStatus.OK);
    }

    //void returns
    @RequestMapping(value = "voidMethod/{input}", method = RequestMethod.DELETE,
            headers = { "Accept=application/xml,application/json" })
    @ResponseStatus(value = HttpStatus.OK, reason = "Just testing")
    public void groomingFunctionThatReturnsVoid(@PathVariable("input") String input) throws Exception {
    }

}
