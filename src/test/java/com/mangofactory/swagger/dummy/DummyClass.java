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

   @ApiOperation(value = "", consumes = "application/xml")
   public void methodWithXmlConsumes(){}

   @ApiOperation(value = "", produces = "application/xml")
   public void methodWithXmlProduces(){}

   @ApiOperation(value = "", produces = "application/xml, application/json", consumes = "application/xml, application/json")
   public void methodWithMultipleMediaTypes(){}

   @ApiOperation(value = "", produces = "application/xml", consumes= "application/xml")
   public void methodWithBothXmlMediaTypes(){}

   @Deprecated
   public void methodWithDeprecated(){}

   public static class ApiIgnorableClass{
      @ApiIgnore
      public void dummyMethod(){}
   }
}


