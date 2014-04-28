package com.mangofactory.swagger.controllers;

import static com.google.common.collect.Iterables.getFirst;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.mangofactory.swagger.annotations.ApiIgnore;
import com.mangofactory.swagger.core.SwaggerCache;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ResourceListing;

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
    @RequestMapping(value = { DOCUMENTATION_BASE_PATH + "/**" }, method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<ApiListing> getApiListing(HttpServletRequest request) {

        final String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        List<String> segments = Splitter.on('/').omitEmptyStrings().splitToList(path);

        if (segments.isEmpty() || segments.size() < 2) {
            new ResponseEntity<ResourceListing>(HttpStatus.NOT_FOUND);
        }

        boolean isFirstSegmentSwaggerGroup = swaggerCache.getResourceListing(segments.get(1)) != null;
        if (isFirstSegmentSwaggerGroup) {
            return getSwaggerApiListing(Joiner.on('/').join(segments.subList(2, segments.size())));
        }

        return getSwaggerApiListing(Joiner.on('/').join(segments.subList(1, segments.size())));
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
