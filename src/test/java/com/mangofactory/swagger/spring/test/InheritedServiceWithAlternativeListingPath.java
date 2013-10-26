package com.mangofactory.swagger.spring.test;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Api(value = "", listingPath = "alternativeChild", description = "ALTERNATIVE - Inherited via Interface Services")
@RequestMapping("anotherChild")
public interface InheritedServiceWithAlternativeListingPath {

    @ApiOperation(value = "ALTERNATIVE - Go ahead and get something, while taking in a parameter", notes = "ALTERNATIVE - This operation is cool")
    @RequestMapping(value = "alternative-child-method", method = RequestMethod.GET)
    public String getSomething(@ApiParam(value = "ALTERNATIVE - The parameter to do stuff with", internalDescription = "ALTERNATIVE - The Coolest Parameter") String parameter);

    public abstract class InheritedServiceWithAlternativeListingPathSuperImpl implements InheritedServiceWithAlternativeListingPath {
        public abstract String getSomethingElse(@ApiParam("Another Parameter") int anotherParameter);
    }

    @Controller
    public class InheritedServiceWithAlternativeListingPathImpl extends InheritedServiceWithAlternativeListingPathSuperImpl {
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