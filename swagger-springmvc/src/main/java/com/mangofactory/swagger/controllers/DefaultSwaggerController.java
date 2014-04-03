package com.mangofactory.swagger.controllers;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.mangofactory.swagger.core.SwaggerCache;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ResourceListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.google.common.collect.Iterables.*;

@Controller
public class DefaultSwaggerController {

    public static final String DOCUMENTATION_BASE_PATH = "/api-docs";
    private static final String API_ROOT = "root";

    @Autowired
    private SwaggerCache swaggerCache;

    @ApiIgnore
    @RequestMapping(value = { DOCUMENTATION_BASE_PATH }, method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<ResourceListing> getResourceListingByKey() {
        return getSwaggerResourceListing(API_ROOT);
    }

    @ApiIgnore
    @RequestMapping(value = { DOCUMENTATION_BASE_PATH + "/{resource}" }, method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<ApiListing> getApiListing(@PathVariable String resource) {
        return getSwaggerApiListing(resource);
    }

    @ApiIgnore
    @RequestMapping(value = { DOCUMENTATION_BASE_PATH + "/{swaggerGroup}/{resource}" }, method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<ApiListing> getApiListing(@PathVariable String swaggerGroup, @PathVariable String resource) {
        return getSwaggerApiListing(resource);
    }

    private ResponseEntity<ApiListing> getSwaggerApiListing(String resource) {
        ResponseEntity<ApiListing> responseEntity = new ResponseEntity<ApiListing>(HttpStatus.NOT_FOUND);
        ApiListing apiListing = swaggerCache.getSwaggerApiListing(resource);
        if (null != apiListing) {
            responseEntity = new ResponseEntity<ApiListing>(apiListing, HttpStatus.OK);
        }
        return responseEntity;
    }

    private ResponseEntity<ResourceListing> getSwaggerResourceListing(String resourceKey) {
        ResponseEntity<ResourceListing> responseEntity = new ResponseEntity<ResourceListing>(HttpStatus.NOT_FOUND);
        ResourceListing resourceListing = null;

        if (API_ROOT.equals(resourceKey)) {
            resourceListing = getFirst(swaggerCache.getSwaggerApiResourceListingMap().values(), null);
        } else {
            if (swaggerCache.getSwaggerApiResourceListingMap().containsKey(resourceKey)) {
                resourceListing = swaggerCache.getSwaggerApiResourceListingMap().get(resourceKey);
            }
        }
        if (null != resourceListing) {
            responseEntity = new ResponseEntity<ResourceListing>(resourceListing, HttpStatus.OK);
        }
        return responseEntity;
    }
}
