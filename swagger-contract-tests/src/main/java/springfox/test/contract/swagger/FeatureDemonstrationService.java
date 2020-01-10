/*
 *
 *  Copyright 2016-2019 the original author or authors.
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

package springfox.test.contract.swagger;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ExampleProperty;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import org.joda.time.LocalDate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.test.contract.swagger.models.Business;
import springfox.test.contract.swagger.models.EnumObjectType;
import springfox.test.contract.swagger.models.EnumType;
import springfox.test.contract.swagger.models.Example;
import springfox.test.contract.swagger.models.FancyPet;
import springfox.test.contract.swagger.models.ModelAttributeExample;
import springfox.test.contract.swagger.models.ModelWithArrayOfArrays;
import springfox.test.contract.swagger.models.ModelWithMapProperty;
import springfox.test.contract.swagger.models.ModelWithObjectNode;
import springfox.test.contract.swagger.models.NestedType;
import springfox.test.contract.swagger.models.Pet;
import springfox.test.contract.swagger.models.PetWithSerializer;
import springfox.test.contract.swagger.models.Vehicle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.*;


@Controller
@RequestMapping("/features")
@Api(value = "", description = "Demonstration of features", basePath = "features")
public class FeatureDemonstrationService {

  //Uses alternate listing path
  @RequestMapping(value = "/{petId}", method = RequestMethod.GET)
  @ApiOperation(value = "Find pet by ID", notes = "Returns a pet when ID < 10. "
      + "ID > 10 or non-integers will simulate API error conditions",
                response = Pet.class,
                extensions = {
                    @Extension(properties = @ExtensionProperty(name = "x-test1", value = "value1")),
                    @Extension(name = "test2", properties = @ExtensionProperty(name = "name2", value = "value2"))
                }
  )
  public Pet getPetById(
      @ApiParam(
          value = "ID of pet that needs to be fetched",
          allowableValues = "range[1,5]",
          required = true,
          example = "3")
      @PathVariable("petId") String petId) {
    throw new RuntimeException("NotImplementedException");
  }

  //Lists all http methods with this operation
  @RequestMapping("/allMethodsAllowed")
  public void allMethodAllowed() {
    throw new RuntimeException("NotImplementedException");
  }

  //Calculates effective url and ignores UriComponentsBuilder
  @RequestMapping(value = "/effective", method = RequestMethod.GET)
  public ResponseEntity<Example> getEffective(UriComponentsBuilder builder) {
    return new ResponseEntity<Example>(
        new Example(
            "Hello",
            1,
            EnumType.ONE,
            new NestedType("test")),
        HttpStatus.OK);
  }

  //Returns nested generic types
  @RequestMapping(value = "/effectives", method = RequestMethod.GET)
  private ResponseEntity<List<Example>> getEffectives() {
    return new ResponseEntity<List<Example>>(
        singletonList(new Example("Hello",
                                  1,
                                  EnumType.ONE,
                                  new NestedType("test"))),
        HttpStatus.OK);
  }

  //No request body annotation or swagger annotation
  @RequestMapping(value = "/enumObject", method = RequestMethod.GET)
  public ResponseEntity<EnumObjectType> getEnumAsObject() {
    return ResponseEntity.ok(EnumObjectType.ONE);
  }

  //No request body annotation or swagger annotation
  @RequestMapping(value = "/bare", method = RequestMethod.POST)
  public void getBare(Example example) {
    //No-op
  }

  //Enum input
  @RequestMapping(value = "/status", method = RequestMethod.POST)
  public void updateBaz(EnumType enumType) {
    //No-op
  }


  //Generic collection input
  @RequestMapping(value = "/statuses", method = RequestMethod.POST)
  public void updateBazes(List<EnumType> enumType) {
    //No-op
  }

  //Generic collection input
  @RequestMapping(value = "/integers", method = RequestMethod.PUT)
  public void updateListOfIntegers(List<Integer> integers) {
    //No-op
  }

  //Generic collection input
  @RequestMapping(value = "/examples", method = RequestMethod.PUT)
  public void updateListOfExamples(List<Example> examples) {
    //No-op
  }

  //LocalDate transformation
  @RequestMapping(value = "/date", method = RequestMethod.POST)
  public void updateDate(LocalDate localDate) {
    //No-op
  }

  //BigDecimal transformation
  @RequestMapping(value = "/bigDecimal", method = RequestMethod.POST)
  public void updateBigDecimal(BigDecimal input) {
    //No-op
  }

  //boolean transformation
  @RequestMapping(value = "/boolean", method = RequestMethod.POST)
  public void updateBoolean(Boolean input) {
    //No-op
  }

  @RequestMapping
  @ApiOperation(value = "all")
  public HttpEntity<String> all() {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "mapProperty", method = RequestMethod.GET)
  public ModelWithMapProperty modelWithMapProperty() {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "listOfMaps", method = RequestMethod.GET)
  public List<Map<String, String>> listOfMaps() {
    throw new UnsupportedOperationException();
  }


  @RequestMapping(value = "mapOfMapOfExample", method = RequestMethod.GET)
  public Map<String, Map<String, Example>> mapOfMapOfExample() {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "addFiles", method = RequestMethod.POST)
  @ApiOperation(value = "Add a new contact with file attachment")
  public ResponseEntity<Void> addFiles(@RequestPart("files") MultipartFile[] files) {
    return new ResponseEntity<Void>(HttpStatus.OK);
  }

  @RequestMapping(value = "/modelAttributes", method = RequestMethod.GET)
  public void getModelAttribute(@ModelAttribute ModelAttributeExample example) {
    //No-op
  }

  @RequestMapping(value = "/arrayOfArrays", method = RequestMethod.POST)
  public int[][] arrayOfArrays(@RequestBody Business.BusinessType[][] arrayOfEnums) {
    return new int[0][0];
  }

  @RequestMapping(value = "/propertyWithArrayOfArrays", method = RequestMethod.POST)
  public void propertyWithArrayOfArrays(@RequestBody ModelWithArrayOfArrays model) {
    //No-op
  }

  @RequestMapping(value = "/propertyWithObjectNode", method = RequestMethod.POST)
  public void propertyWithObjectNode(@RequestBody ModelWithObjectNode model) {
    //No-op
  }

  @RequestMapping(value = "/1430-body", method = RequestMethod.POST)
  public void base64EncodedBody(@RequestBody byte[] base64Encoded) {
    //No-op
  }

  @RequestMapping(value = "/1430-query", method = RequestMethod.POST)
  public void proper(@RequestParam byte[] base64Encoded) {
    //No-op
  }

  @RequestMapping(value = "/1367/{itemId}", method = RequestMethod.GET, produces = "application/vnd.com.pet+json")
  public ResponseEntity<Pet> findIdentityById(@PathVariable String itemId) {
    return new ResponseEntity<Pet>(
        new Pet(),
        HttpStatus.OK);
  }

  @RequestMapping(value = "/1367/{itemId}", method = RequestMethod.GET, produces = "application/vnd.com.fancy-pet+json")
  public ResponseEntity<FancyPet> findById(@PathVariable String itemId) {
    return new ResponseEntity<FancyPet>(
        new FancyPet(),
        HttpStatus.OK);
  }

  @RequestMapping(value = "/1490/entity/{itemId}", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<PetWithSerializer> serializablePetEntity(@PathVariable String itemId) {
    return new ResponseEntity<PetWithSerializer>(
        new PetWithSerializer(),
        HttpStatus.OK);
  }

  @RequestMapping(value = "/1490/{itemId}", method = RequestMethod.GET)
  @ResponseBody
  public PetWithSerializer serializablePet(@PathVariable String itemId) {
    return new PetWithSerializer();
  }

  @RequestMapping(value = "/1490/{itemId}", method = RequestMethod.PUT)
  public void updateSerializablePet(
      @PathVariable String itemId,
      @RequestBody PetWithSerializer pet) {
  }

  @GetMapping(value = "/inheritance")
  public List<Vehicle> findVehicles(@RequestParam("type") String type) {
    return new ArrayList<Vehicle>();
  }

  // tag::question-27[]
  @RequestMapping(value = "/2031", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "/2031")
  @ApiImplicitParams({
                         @ApiImplicitParam(
                             name = "contents",
                             dataType = "CustomTypeFor2031",
                             examples = @io.swagger.annotations.Example(
                                 value = {
                                     @ExampleProperty(value = "{'property': 'test'}", mediaType = "application/json")
                                 })) //<1>
                     })
  public void save(
      @PathVariable("keyId") String keyId,
      @PathVariable("id") String id,
      @RequestBody String contents
      //<2>
                  ) {
  }

  public static class CustomTypeFor2031 { //<3>
    private String property;

    public String getProperty() {
      return property;
    }

    public void setProperty(String property) {
      this.property = property;
    }
  }
  // end::question-27[]

  @RequestMapping(value = "/1570", method = RequestMethod.POST)
  @ApiOperation(value = "Demo using examples")
  @ApiResponses(value = { @ApiResponse(code = 404, message = "User not found"),
      @ApiResponse(
          code = 405,
          message = "Validation exception",
          examples = @io.swagger.annotations.Example(
              value = {
                  @ExampleProperty(
                      mediaType = "Example json",
                      value = "{'invalidField': 'address'}"),
                  @ExampleProperty(
                      mediaType = "Example string",
                      value = "The first name was invalid") })) })
  public void saveUser() {
    //No-op
  }
}
