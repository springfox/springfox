package com.mangofactory.swagger.dummy;

public class DummyModels {

   public class BusinessModel{
      private String name;
      private String numEmployees;

      public BusinessModel() {
      }

      public String getNumEmployees() {
         return numEmployees;
      }

      public void setNumEmployees(String numEmployees) {
         this.numEmployees = numEmployees;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }
   }

   public class CorporationModel extends BusinessModel{

   }

}
