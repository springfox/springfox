package com.mangofactory.swagger.spring.test;

import com.mangofactory.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.sample.exception.NotFoundException;
import org.apache.commons.lang.NotImplementedException;
import org.joda.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.*;

@Controller
@RequestMapping("/features")
@Api(value="", description="Demonstration of features", listingPath = "features")
public class FeatureDemonstrationService {

    //Uses alternate listing path
    @RequestMapping(value="/{petId}",method=RequestMethod.GET)
	@ApiOperation(value = "Find pet by ID", notes = "Returns a pet when ID < 10. "
			+ "ID > 10 or nonintegers will simulate API error conditions",
            responseClass = "ccom.mangofactory.swagger.spring.test.Pet"
		)
	@ApiErrors(NotFoundException.class)
	public Pet getPetById (
			@ApiParam(value = "ID of pet that needs to be fetched",  allowableValues = "range[1,5]", required = true)
            @PathVariable("petId") String petId)
	throws NotFoundException {
		throw new NotImplementedException();
	}

    //Lists all http methods with this operation
	@RequestMapping("/allMethodsAllowed")
	public void allMethodAllowed() {
		throw new NotImplementedException();
	}

    //Calculates effective url and ignores UriComponentsBuilder
    @RequestMapping(value = "/effective", method = RequestMethod.GET)
    public ResponseEntity<Example> getEffective(UriComponentsBuilder builder) {
        return new ResponseEntity<Example>(new Example("Hello", 1, EnumType.ONE, new NestedType("test")), HttpStatus.OK);
    }

    //Returns nested generic types
    @RequestMapping(value = "/effectives", method = RequestMethod.GET)
    private ResponseEntity<List<Example>> getEffectives() {
        return new ResponseEntity<List<Example>>(newArrayList(new Example("Hello", 1, EnumType.ONE,
                new NestedType("test"))),
                HttpStatus.OK);
    }

    //No request body annotation or swagger annotation
    @RequestMapping(value = "/effective", method = RequestMethod.POST)
    public void getBare(Example example) {
        //No-op
    }

    //Enum input
    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public void updateBaz(EnumType enumType) {
        //No-op
    }


    //Generic ollection input
    @RequestMapping(value = "/statuses", method = RequestMethod.POST)
    public void updateBazes(Collection<EnumType> enumType) {
        //No-op
    }

    //LocalDate transformation
    @RequestMapping(value = "/date", method = RequestMethod.POST)
    public void updateDate(LocalDate localDate) {
        //No-op
    }

    //BigDecimal transformation
    @RequestMapping(value = "/bigDecimal", method = RequestMethod.POST)
    public void updateBigDecimal(BigDecimal input) {
        //No-op
    }
		
}
