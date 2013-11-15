package com.mangofactory.swagger.dummy;

import com.mangofactory.swagger.annotations.ApiIgnore;

public class DummyClass {
   public void dummyMethod(){}


   public static class ApiIgnorableClass{
      @ApiIgnore
      public void dummyMethod(){}
   }
}


