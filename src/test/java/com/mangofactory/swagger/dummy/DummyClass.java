package com.mangofactory.swagger.dummy;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;
import java.util.Date;

public class DummyClass {
   public void dummyMethod() {
   }

   @ApiOperation(value = "description", httpMethod = "GET")
   public void methodWithHttpGETMethod() {
   }

   @ApiOperation(value = "description", httpMethod = "RUBBISH")
   public void methodWithInvalidHttpMethod() {
   }

   @ApiOperation(value = "summary", httpMethod = "RUBBISH")
   public void methodWithSummary() {
   }

   @ApiOperation(value = "", notes = "some notes")
   public void methodWithNotes() {
   }

   @ApiOperation(value = "", position = 5)
   public void methodWithPosition() {
   }

   @ApiOperation(value = "", consumes = "application/xml")
   public void methodWithXmlConsumes() {
   }

   @ApiOperation(value = "", produces = "application/xml")
   public void methodWithXmlProduces() {
   }

   @ApiOperation(value = "", produces = "application/xml, application/json", consumes = "application/xml, application/json")
   public void methodWithMultipleMediaTypes() {
   }

   @ApiOperation(value = "", produces = "application/xml", consumes = "application/xml")
   public void methodWithBothXmlMediaTypes() {
   }

   @ApiOperation(value = "", response = DummyModels.FunkyBusiness.class)
   public void methodApiResponseClass() {
   }

   @ApiOperation(value = "")
   public DummyModels.FunkyBusiness methodWithAPiAnnotationButWithoutResponseClass() {
      return null;
   }

   @ApiOperation(value = "")
   public DummyModels.Paginated<BusinessType> methodWithGenericType() {
      return null;
   }

   @Deprecated
   public void methodWithDeprecated() {
   }

   public void methodWithServletRequest(ServletRequest req) {
   }

   public void methodWithBindingResult(BindingResult res) {
   }

   public void methodWithInteger(Integer integer) {
   }

   @RequestMapping("/businesses/{businessId}")
   public void methodWithSinglePathVariable(@PathVariable String businessId) {

   }

   @RequestMapping("/businesses/{businessId}")
   public void methodWithSingleEnum(BusinessType businessType) {

   }
   @ResponseBody
   public DummyModels.BusinessModel methodWithConcreteResponseBody() {
      return null;
   }

   @ResponseBody
   public DummyModels.AnnotatedBusinessModel methodWithModelAnnotations() {
      return null;
   }

   @ResponseBody
   public DummyModels.CorporationModel methodWithConcreteCorporationModel() {
      return null;
   }

   @ResponseBody
   public Date methodWithDateResponseBody() {
      return null;
   }

   @ApiResponses({ @ApiResponse(code = 413, message = "a message")})
   public void methodWithApiResponses(){}
   public static class ApiIgnorableClass {
      @ApiIgnore
      public void dummyMethod() {
      }
   }

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
   public class CustomClass{}
}


