package com.mangofactory.swagger.spring.test;

import com.wordnik.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/fancypets")
@Api(value="", description="Operations about fancy pets")
public class FancyPetService extends AbstractPetService<FancyPet> {

    // some subclass dependency here
    // override one of superclass
    @Override
    public @ResponseBody
    int createObject(@RequestBody FancyPet object) {
        int id = super.createObject(object);
        // do some logic with sub class
        return id;
    }


    //Example of generic type constraint
    @RequestMapping(method = RequestMethod.PUT)
    public <T extends Pet> void updatePet(@RequestBody T pet) {
        throw new UnsupportedOperationException();
    }

    // overload one of superclass
    public @ResponseBody
    @RequestMapping(method= RequestMethod.POST, value = "?{someId}")
    int createObject(@RequestBody FancyPet object, @PathVariable int someId) {
        int id = super.createObject(object);
        // do some logic with sub class
        return id;
    }
}
