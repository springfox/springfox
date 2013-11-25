package com.mangofactory.swagger.dummy;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.ApiOperation;

public class DummyClass {
   public void dummyMethod(){}

   @ApiOperation( value = "description", httpMethod = "GET")
   public void methodWithHttpGETMethod(){}

   @ApiOperation( value = "description", httpMethod = "RUBBISH")
   public void methodWithInvalidHttpMethod(){}

   @ApiOperation( value = "summary", httpMethod = "RUBBISH")
   public void methodWithSummary(){}

   @ApiOperation( value = "", notes = "some notes")
   public void methodWithNotes(){}

   @ApiOperation( value = "", position = 5)
   public void methodWithPosition(){}


   public static class ApiIgnorableClass{
      @ApiIgnore
      public void dummyMethod(){}
   }
}


