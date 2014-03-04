package com.mangofactory.swagger.dummy;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static com.mangofactory.swagger.dummy.DummyModels.Ignorable;

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

   @ApiOperation(value = "SomeVal",
           authorizations = @Authorization(value = "oauth2",
                   scopes = {@AuthorizationScope(scope = "scope", description = "scope description")
                   }))
   public void methodWithAuth() {
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

   public void methodWithAnnotatedInteger(@Ignorable Integer integer) {
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

   public void methodParameterWithRequestBodyAnnotation(
           @RequestBody DummyModels.BusinessModel model,
           HttpServletResponse response,
           DummyModels.AnnotatedBusinessModel annotatedBusinessModel){
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

    class MethodsWithSameName {
        public ResponseEntity methodToTest(Integer integer, Parent child) {
            return null;
        }
        public void methodToTest(Integer integer, Child child) {
        }
    }

    class Parent {

    }

    class Child extends Parent {

    }
}


