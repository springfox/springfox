package com.mangofactory.swagger.dummy;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.mangofactory.swagger.dummy.models.Example;
import com.mangofactory.swagger.dummy.models.FoobarDto;
import com.mangofactory.swagger.dummy.models.Treeish;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.annotations.Authorization;
import com.wordnik.swagger.annotations.AuthorizationScope;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static com.mangofactory.swagger.dummy.DummyModels.*;

public class DummyClass {
  public void dummyMethod() {
  }

  @ApiOperation(value = "description", httpMethod = "GET")
  public void methodWithHttpGETMethod() {
  }

  @ApiOperation(value = "description", httpMethod = "GET", hidden = true)
  public void methodThatIsHidden() {
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

  @ApiOperation(value = "", produces = "application/xml, application/json", consumes = "application/xml, " +
          "application/json")
  public void methodWithMultipleMediaTypes() {
  }

  @ApiOperation(value = "", produces = "application/xml", consumes = "application/xml")
  public void methodWithBothXmlMediaTypes() {
  }

  @ApiOperation(value = "", produces = "application/json", consumes = "application/xml")
  public void methodWithMediaTypeAndFile(MultipartFile multipartFile) {
  }

  @ApiOperation(value = "", response = DummyModels.FunkyBusiness.class)
  public void methodApiResponseClass() {
  }

  @ApiResponses({
          @ApiResponse(code = 201, response = Void.class, message = "Rule Scheduled successfuly"),
          @ApiResponse(code = 500, response = RestError.class, message = "Internal Server Error"),
          @ApiResponse(code = 406, response = RestError.class, message = "Not acceptable")})
  public void methodAnnotatedWithApiResponse() {
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

  public ResponseEntity<byte[]> methodWithGenericPrimitiveArray() {
    return null;
  }

  public ResponseEntity<DummyClass[]> methodWithGenericComplexArray() {
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

  public void methodWithModelAttribute(@ModelAttribute Example example) {
  }

  public void methodWithoutModelAttribute(Example example) {
  }

  public void methodWithTreeishModelAttribute(@ModelAttribute Treeish example) {
  }

  @RequestMapping("/businesses/{businessId}")
  public void methodWithSinglePathVariable(@PathVariable String businessId) {

  }

  @RequestMapping("/businesses/{businessId}")
  public void methodWithSingleEnum(BusinessType businessType) {

  }

  @RequestMapping("/businesses/{businessId}")
  public void methodWithSingleEnumArray(BusinessType[] businessTypes) {

  }

  @RequestMapping("/businesses/{businessId}/employees/{employeeId}/salary")
  public void methodWithRatherLongRequestPath() {

  }

  @RequestMapping(value = "/parameter-conditions", params = "test=testValue")
  public void methodWithParameterRequestCondition() {

  }

  @ApiImplicitParam(name = "Authentication", dataType = "string", required = true, paramType = "header",
          value = "Authentication token")
  public void methodWithApiImplicitParam() {
  }

  @ApiImplicitParam(name = "Authentication", dataType = "string", required = true, paramType = "header",
          value = "Authentication token")
  public void methodWithApiImplicitParamAndInteger(Integer integer) {
  }

  @ApiImplicitParams({
          @ApiImplicitParam(name = "lang", dataType = "string", required = true, paramType = "query",
                  value = "Language", defaultValue = "EN", allowableValues = "EN,FR"),
          @ApiImplicitParam(name = "Authentication", dataType = "string", required = true, paramType = "header",
                  value = "Authentication token")
  })
  public void methodWithApiImplicitParams(Integer integer) {
  }

  public interface ApiImplicitParamsInterface {
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lang", dataType = "string", required = true, paramType = "query",
                    value = "Language", defaultValue = "EN", allowableValues = "EN,FR")
    })
    @ApiImplicitParam(name = "Authentication", dataType = "string", required = true, paramType = "header",
            value = "Authentication token")
    void methodWithApiImplicitParam();
  }

  public static class ApiImplicitParamsClass implements ApiImplicitParamsInterface {
    @Override
    public void methodWithApiImplicitParam() {
    }
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
          DummyModels.AnnotatedBusinessModel annotatedBusinessModel) {
  }

  @ResponseBody
  public DummyModels.AnnotatedBusinessModel methodWithSameAnnotatedModelInReturnAndRequestBodyParam(
          @RequestBody DummyModels.AnnotatedBusinessModel model) {
    return null;
  }

  @ApiResponses({@ApiResponse(code = 413, message = "a message")})
  public void methodWithApiResponses() {
  }

  @ApiIgnore
  public static class ApiIgnorableClass {
    @ApiIgnore
    public void dummyMethod() {
    }
  }

  @ResponseBody
  public DummyModels.ModelWithSerializeOnlyProperty methodWithSerializeOnlyPropInReturnAndRequestBodyParam(
          @RequestBody DummyModels.ModelWithSerializeOnlyProperty model) {
    return null;
  }

  @ResponseBody
  public FoobarDto methodToTestFoobarDto(@RequestBody FoobarDto model) {
    return null;
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

  public class CustomClass {
  }

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


