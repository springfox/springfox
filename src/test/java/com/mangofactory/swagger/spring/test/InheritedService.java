package com.mangofactory.swagger.spring.test;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Api(value = "", description = "Inherited via Interface Services")
@RequestMapping("child")
public interface InheritedService {

    @ApiOperation(value = "Go ahead and get something, while taking in a parameter", notes = "This operation is cool")
    @RequestMapping(value = "child-method", method = RequestMethod.GET)
    public String getSomething(@ApiParam(value = "The parameter to do stuff with", internalDescription = "The Coolest Parameter") String parameter);

    public abstract class InheritedServiceSuperImpl implements InheritedService {
        public abstract String getSomethingElse(@ApiParam("Another Parameter") int anotherParameter);
    }

    @Controller
    public class InheritedServiceImpl extends InheritedServiceSuperImpl {
        @Override
        public String getSomething(String parameter) {
            return parameter;
        }

        @Override
        public String getSomethingElse(int anotherParameter) {
            return "Hello World: " + anotherParameter;
        }
    }

}