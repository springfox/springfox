package com.mangofactory.swagger.spring.test;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mangofactory.swagger.annotations.ApiError;
import com.mangofactory.swagger.annotations.ApiErrors;
import com.mangofactory.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.sample.exception.NotFoundException;

@Controller
@RequestMapping("/pets")
@Api(value="", description="Operations about pets")
public class PetService {

    @RequestMapping(value="/{petId}",method=RequestMethod.GET)
    @ApiOperation(value = "Find pet by ID", notes = "Returns a pet when ID < 10. "
            + "ID > 10 or nonintegers will simulate API error conditions", responseClass = "ccom.mangofactory.swagger.spring.test.Pet"
        )
    @ApiErrors(NotFoundException.class)
    public Pet getPetById (
            @ApiParam(value = "ID of pet that needs to be fetched",  allowableValues = "range[1,5]", required = true) @PathVariable("petId") String petId) 
    throws NotFoundException {
        throw new NotImplementedException();
    }

    @RequestMapping(method=RequestMethod.POST)
    @ApiOperation(value = "Add a new pet to the store")
    @ApiErrors(errors = { @ApiError(code = 405, reason = "Invalid input") })
    public void addPet(
            @ApiParam(value = "Pet object that needs to be added to the store", required = true) Pet pet) {
        throw new NotImplementedException();
    }

    @RequestMapping(method=RequestMethod.PUT)
    @ApiOperation(value = "Update an existing pet")
    @ApiErrors(errors = { @ApiError(code = 400, reason = "Invalid ID supplied"),
            @ApiError(code = 404, reason = "Pet not found"),
            @ApiError(code = 405, reason = "Validation exception") })
    public void updatePet(
            @ApiParam(value = "Pet object that needs to be added to the store", required = true) @RequestBody Pet pet) {
        throw new NotImplementedException();
    }

    @RequestMapping(value="/findByStatus",method=RequestMethod.GET)
    @ApiOperation(value = "Finds Pets by status", notes = "Multiple status values can be provided with comma seperated strings", responseClass = "ccom.mangofactory.swagger.spring.test.Pet", multiValueResponse = true)
    @ApiErrors(errors = { @ApiError(code = 400, reason = "Invalid status value") })
    public void findPetsByStatus(
            @ApiParam(value = "Status values that need to be considered for filter", required = true, defaultValue = "available", allowableValues = "available,pending,sold", allowMultiple = true) @RequestParam("status") String status) {
        throw new NotImplementedException();
    }

    @RequestMapping(value="/findByTags",method=RequestMethod.GET)
    @ApiOperation(value = "Finds Pets by tags", notes = "Muliple tags can be provided with comma seperated strings. Use tag1, tag2, tag3 for testing.", responseClass = "ccom.mangofactory.swagger.spring.test.Pet", multiValueResponse = true)
    @ApiErrors(errors = { @ApiError(code = 400, reason = "Invalid tag value") })
    @Deprecated
    public void findPetsByTags(
            @ApiParam(value = "Tags to filter by", required = true, allowMultiple = true) @RequestParam("tags") String tags) {
        throw new NotImplementedException();
    }
    
    @RequestMapping("/allMethodsAllowed")
    public void allMethodAllowed() {
        throw new NotImplementedException();
    }

    @RequestMapping(value="/contrivedPetLookupExample", method=RequestMethod.POST)
    @ApiModel(type = Pet.class, collection = true)
    public List<Pet> contrivedPetLookupExample(@ApiModel(type = Pet.class) Pet pet) {
        throw new NotImplementedException();
    }
    
    @RequestMapping(value="/collectionExample", method=RequestMethod.POST)
    @ApiModel(type = Pet.class, collection = true)
    public Collection<Pet> collectionExample(@ApiModel(type = Pet.class) Collection<Pet> pet) {
        throw new NotImplementedException();
    }
        
}
