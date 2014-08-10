//Copyright 2014 Choice Hotels International
package com.mangofactory.swagger.dummy.models;

import com.wordnik.swagger.annotations.ApiParam;

public class Person {

    @ApiParam(value = "The first name", required = true)
    private String firstName;

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

}
