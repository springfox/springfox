package com.mangofactory.swagger.dummy.models;

import org.joda.time.LocalDate;

import java.util.Date;


public class Business {
   //not private - just for testing
   public int id;
   public String name;
   public String owner;
   public LocalDate inception;
   public BusinessType businessType = BusinessType.PRODUCT;
   public Date taxDate = new Date();

   public enum BusinessType {
      PRODUCT(1),
      SERVICE(2);
      private int value;

      private BusinessType(int value) {
         this.value = value;
      }

      public int getValue() {
         return value;
      }
   }
}


