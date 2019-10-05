/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonView;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.schema.Views;
import springfox.documentation.spring.web.dummy.DummyModels.Ignorable;
import springfox.documentation.spring.web.dummy.models.EnumType;
import springfox.documentation.spring.web.dummy.models.Example;
import springfox.documentation.spring.web.dummy.models.FancyPet;
import springfox.documentation.spring.web.dummy.models.FoobarDto;
import springfox.documentation.spring.web.dummy.models.MapFancyPet;
import springfox.documentation.spring.web.dummy.models.Pet;
import springfox.documentation.spring.web.dummy.models.PetWithJsonView;
import springfox.documentation.spring.web.dummy.models.Pirate;
import springfox.documentation.spring.web.dummy.models.RecursiveTypeWithConditions;
import springfox.documentation.spring.web.dummy.models.RecursiveTypeWithNonEqualsConditionsOuter;
import springfox.documentation.spring.web.dummy.models.RecursiveTypeWithNonEqualsConditionsOuterWithSubTypes;
import springfox.documentation.spring.web.dummy.models.SameFancyPet;
import springfox.documentation.spring.web.dummy.models.Treeish;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequestMapping(produces = { "application/json" }, consumes = { "application/json", "application/xml" })
@ApiResponses({
                  @ApiResponse(code = 404, response = RestError.class, message = "Not Found")
              })
public class DummyClass {


  @ApiParam
  public void annotatedWithApiParam() {
    throw new UnsupportedOperationException();
  }

  public void dummyMethod() {
    throw new UnsupportedOperationException();
  }

  public void methodWithOneArgs(int a) {
    throw new UnsupportedOperationException();
  }

  public void methodWithTwoArgs(
      int a,
      String b) {
    throw new UnsupportedOperationException();
  }

  public void methodWithNoArgs() {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "description", httpMethod = "GET")
  public void methodWithHttpGETMethod() {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "description", nickname = "unique")
  public void methodWithNickName() {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "description", httpMethod = "GET", hidden = true)
  public void methodThatIsHidden() {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "description", httpMethod = "RUBBISH")
  public void methodWithInvalidHttpMethod() {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "summary", httpMethod = "RUBBISH")
  public void methodWithSummary() {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "", notes = "some notes")
  public void methodWithNotes() {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "", nickname = "a nickname")
  public void methodWithNickname() {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "", position = 5)
  public void methodWithPosition() {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "", consumes = "application/xml")
  public void methodWithXmlConsumes() {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "", produces = "application/xml")
  public void methodWithXmlProduces() {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "", produces = "application/xml, application/json", consumes = "application/xml, " +
      "application/json")
  public void methodWithMultipleMediaTypes() {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "", produces = "application/xml", consumes = "application/xml")
  public void methodWithBothXmlMediaTypes() {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "", produces = "application/json", consumes = "application/xml")
  public void methodWithMediaTypeAndFile(MultipartFile multipartFile) {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "", response = DummyModels.FunkyBusiness.class)
  public void methodApiResponseClass() {
    throw new UnsupportedOperationException();
  }

  @ApiResponses({
                    @ApiResponse(code = 201, response = Void.class, message = "Rule Scheduled successfully"),
                    @ApiResponse(code = 500, response = RestError.class, message = "Internal Server Error"),
                    @ApiResponse(code = 406, response = RestError.class, message = "Not acceptable") })
  public void methodAnnotatedWithApiResponse() {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "methodWithExtensions",
                extensions = {
                    @Extension(properties = @ExtensionProperty(name = "x-test1", value = "value1")),
                    @Extension(name = "test2", properties = @ExtensionProperty(name = "name2", value = "value2"))
                }
  )
  public void methodWithExtensions() {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "SomeVal",
                authorizations = @Authorization(value = "oauth2",
                                                scopes = { @AuthorizationScope(scope = "scope",
                                                                               description = "scope description")
                                                }))
  public void methodWithAuth() {
    throw new UnsupportedOperationException();
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

  public byte[] methodWithByteArray() {
    return null;
  }

  public ResponseEntity<DummyClass[]> methodWithGenericComplexArray() {
    return null;
  }

  public ResponseEntity<EnumType> methodWithEnumResponse() {
    return null;
  }

  public ResponseEntity<EnumType> methodWithAlternateType(AlternateTypeContainer container) {
    return null;
  }

  @Deprecated
  public void methodWithDeprecated() {
    throw new UnsupportedOperationException();
  }

  public void methodWithServletRequest(ServletRequest req) {
    throw new UnsupportedOperationException();
  }

  public void methodWithBindingResult(BindingResult res) {
    throw new UnsupportedOperationException();
  }

  public void methodWithInteger(Integer integer) {
    throw new UnsupportedOperationException();
  }

  public void methodWithAnnotatedInteger(@Ignorable Integer integer) {
    throw new UnsupportedOperationException();
  }

  public void methodWithURIAsRequestParam(@RequestParam URI uri) {
    throw new UnsupportedOperationException();
  }

  public void methodWithURIAsPathVariable(@PathVariable URI uri) {
    throw new UnsupportedOperationException();
  }

  public void methodWithModelAttribute(@ModelAttribute Example example) {
    throw new UnsupportedOperationException();
  }

  public void methodWithoutModelAttribute(Example example) {
    throw new UnsupportedOperationException();
  }

  public void methodWithTreeishModelAttribute(@ModelAttribute Treeish example) {
    throw new UnsupportedOperationException();
  }

  @RequestMapping("/businesses/{businessId}")
  public void methodWithSinglePathVariable(@PathVariable String businessId) {
    throw new UnsupportedOperationException();

  }

  @RequestMapping("/businesses/{businessId}")
  public void methodWithSingleEnum(BusinessType businessType) {
    throw new UnsupportedOperationException();

  }

  @RequestMapping("/businesses/{businessId}")
  public void methodWithSingleEnumArray(BusinessType[] businessTypes) {
    throw new UnsupportedOperationException();

  }

  @RequestMapping("/businesses/{businessId}/employees/{employeeId}/salary")
  public void methodWithRatherLongRequestPath() {
    throw new UnsupportedOperationException();

  }

  @RequestMapping(value = "/parameter-conditions", params = "test=testValue")
  public void methodWithParameterRequestCondition() {

  }

  @ApiImplicitParam(name = "Authentication", dataType = "string", required = true, paramType = "header",
                    value = "Authentication token")
  public void methodWithApiImplicitParam() {
    throw new UnsupportedOperationException();
  }

  @ApiImplicitParam(name = "Authentication", dataType = "string", required = true, paramType = "header",
                    value = "Authentication token")
  public void methodWithApiImplicitParamAndInteger(Integer integer) {
    throw new UnsupportedOperationException();
  }

  @ApiImplicitParam(name = "Authentication", dataType = "Example", required = true, paramType = "header",
                    value = "Authentication token")
  public void methodWithApiImplicitParamAndExample(Integer integer) {
    throw new UnsupportedOperationException();
  }

  @ApiImplicitParam(name = "Authentication", dataType = "string", required = true, paramType = "header",
                    value = "Authentication token", allowMultiple = true)
  public void methodWithApiImplicitParamAndAllowMultiple(Integer integer) {
    throw new UnsupportedOperationException();
  }

  @ApiImplicitParams({
                         @ApiImplicitParam(name = "lang", dataType = "string", required = true, paramType = "query",
                                           value = "Language", defaultValue = "EN", allowableValues = "EN,FR"),
                         @ApiImplicitParam(name = "Authentication",
                                           dataType = "string",
                                           required = true,
                                           paramType = "header",
                                           value = "Authentication token")
                     })
  public void methodWithApiImplicitParams(Integer integer) {
    throw new UnsupportedOperationException();
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

  @ApiImplicitParams({
          @ApiImplicitParam(name = "common-header", dataType = "string", required = true, paramType = "header")
  })
  public class ApiImplicitParamsAllowMultipleClass implements ApiImplicitParamsInterface {
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
    throw new UnsupportedOperationException();
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
    throw new UnsupportedOperationException();
  }

  public void methodParameterWithRequestPartAnnotation(
      @RequestPart DummyModels.BusinessModel model,
      HttpServletResponse response,
      DummyModels.AnnotatedBusinessModel annotatedBusinessModel) {
    throw new UnsupportedOperationException();
  }

  public void methodParameterWithRequestPartAnnotationOnSimpleType(
      @RequestPart String model,
      HttpServletResponse response,
      DummyModels.AnnotatedBusinessModel annotatedBusinessModel) {
    throw new UnsupportedOperationException();
  }

  @ResponseBody
  public DummyModels.AnnotatedBusinessModel methodWithSameAnnotatedModelInReturnAndRequestBodyParam(
      @RequestBody DummyModels.AnnotatedBusinessModel model) {
    return null;
  }

  @ApiResponses({ @ApiResponse(code = 413, message = "a message") })
  public void methodWithApiResponses() {
    throw new UnsupportedOperationException();
  }

  @ApiIgnore
  public static class ApiIgnorableClass {
    @ApiIgnore
    public void dummyMethod() {
      throw new UnsupportedOperationException();
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

  @ResponseBody
  public Pirate methodToTestBidirectionalRecursiveTypes(@RequestBody Pirate model) {
    throw new UnsupportedOperationException();
  }

  @ResponseBody
  public RecursiveTypeWithConditions methodToTestBidirectionalRecursiveTypesWithConditions(
      @RequestBody RecursiveTypeWithConditions model) {
    throw new UnsupportedOperationException();
  }

  @ResponseBody
  public RecursiveTypeWithNonEqualsConditionsOuter methodToTestBidirectionalRecursiveTypesWithNonEqualsConditions(
      @RequestBody RecursiveTypeWithNonEqualsConditionsOuter model) {
    throw new UnsupportedOperationException();
  }

  @ResponseBody
  public RecursiveTypeWithNonEqualsConditionsOuterWithSubTypes methodToTestBidirectionalRecursiveTypesWithKnownTypes(
      @RequestBody RecursiveTypeWithConditions model) {
    throw new UnsupportedOperationException();
  }

  @ResponseBody
  public Pet methodToTestIssue182(@RequestBody springfox.documentation.spring.web.dummy.models.same.Pet pet) {
    throw new UnsupportedOperationException();
  }

  @ResponseBody
  public Map<String, List<FancyPet>> methodToTestSerializationAndDeserialization(
      @RequestBody Map<String, FancyPet> pet) {
    throw new UnsupportedOperationException();
  }

  @ResponseBody
  public MapFancyPet methodToTestSameClassesWithDifferentProperties(@RequestBody SameFancyPet fancyPet) {
    throw new UnsupportedOperationException();
  }

  @ResponseBody
  @JsonView(Views.SecondView.class)
  public PetWithJsonView methodToTestJsonView(@RequestBody @JsonView(Views.FirstView.class) PetWithJsonView pet) {
    throw new UnsupportedOperationException();
  }

  @ResponseBody
  public List<DummyModels.BusinessModel> methodToTestOrdering(@RequestBody SameFancyPet fancyPet) {
    throw new UnsupportedOperationException();
  }

  @ResponseBody
  public List<DummyModels.BusinessModel> methodToTestOrdering(@RequestBody SameFancyPet fancyPet,
      @RequestParam String id) {
    throw new UnsupportedOperationException();
  }

  @ResponseBody
  public List<DummyModels.BusinessModel> methodToTestOrdering(@RequestParam String id) {
    throw new UnsupportedOperationException();
  }

  public enum BusinessType {
    PRODUCT(1),
    SERVICE(2);
    private int value;

    BusinessType(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }

  public class CustomClass {
  }

  public class MethodsWithSameName {
    public ResponseEntity methodToTest(
        Integer integer,
        Parent child) {
      return null;
    }

    public void methodToTest(
        Integer integer,
        Child child) {
      throw new UnsupportedOperationException();
    }

  }

  public class MethodResolutionToDemonstrate1241 {
    public DTO<String>[] loadDetails(String id) {
      return null;
    }

    public DTO<String>[] loadDetails(
        String id,
        Date since) {
      return null;
    }
  }

  public class DTO<T> {

  }

  class Parent {

  }

  class Child extends Parent {

  }
}