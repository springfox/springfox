/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.spring.web.dummy;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.dummy.DummyModels.Ignorable;
import springfox.documentation.spring.web.dummy.models.EnumType;
import springfox.documentation.spring.web.dummy.models.Example;
import springfox.documentation.spring.web.dummy.models.FoobarDto;
import springfox.documentation.spring.web.dummy.models.Treeish;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequestMapping(produces = {"application/json"}, consumes = {"application/json", "application/xml"})
public class DummyClass {


  @ApiParam
  public void annotatedWithApiParam() {
  }

  public void dummyMethod() {
  }

  public void methodWithOneArgs(int a) {
  }

  public void methodWithTwoArgs(int a, String b) {
  }

  public void methodWithNoArgs() {
  }

  @ApiOperation(value = "description", httpMethod = "GET")
  public void methodWithHttpGETMethod() {
  }

  @ApiOperation(value = "description", nickname = "unique")
  public void methodWithNickName() {
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

  @ApiOperation(value = "", nickname = "a nickname")
  public void methodWithNickname() {
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

  @ApiOperation(value = "methodWithExtensions",
      extensions = {
          @Extension(properties = @ExtensionProperty(name="x-test1", value="value1")),
          @Extension(name="test2", properties = @ExtensionProperty(name="name2", value="value2"))
      }
  )
  public void methodWithExtensions() {
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

  public ResponseEntity<EnumType> methodWithEnumResponse() {
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
  public Map<String, DummyModels.BusinessModel> methodWithMapReturn() {
    return null;
  }

  @ResponseBody
  @ResponseStatus(value = HttpStatus.ACCEPTED, reason = "Accepted request")
  public DummyModels.BusinessModel methodWithResponseStatusAnnotation() {
    return null;
  }

  @ResponseBody
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void methodWithResponseStatusAnnotationAndEmptyReason() {
  }

  @ResponseBody
  public DummyModels.AnnotatedBusinessModel methodWithModelPropertyAnnotations() {
    return null;
  }

  @ResponseBody
  public DummyModels.NamedBusinessModel methodWithModelAnnotations() {
    return null;
  }

  @ResponseBody
  public List<DummyModels.BusinessModel> methodWithListOfBusinesses() {
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

  public void methodParameterWithRequestPartAnnotation(
          @RequestPart DummyModels.BusinessModel model,
          HttpServletResponse response,
          DummyModels.AnnotatedBusinessModel annotatedBusinessModel) {
  }

  public void methodParameterWithRequestPartAnnotationOnSimpleType(
          @RequestPart String model,
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

  public class MethodsWithSameName {
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


