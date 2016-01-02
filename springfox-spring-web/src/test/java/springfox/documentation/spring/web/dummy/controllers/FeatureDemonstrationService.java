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

package springfox.documentation.spring.web.dummy.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import org.joda.time.LocalDate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.spring.web.dummy.models.Business;
import springfox.documentation.spring.web.dummy.models.EnumType;
import springfox.documentation.spring.web.dummy.models.Example;
import springfox.documentation.spring.web.dummy.models.ModelAttributeExample;
import springfox.documentation.spring.web.dummy.models.ModelWithArrayOfArrays;
import springfox.documentation.spring.web.dummy.models.ModelWithMapProperty;
import springfox.documentation.spring.web.dummy.models.NestedType;
import springfox.documentation.spring.web.dummy.models.Pet;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;

@Controller
@RequestMapping("/features")
@Api(value = "", description = "Demonstration of features", basePath = "features")
public class FeatureDemonstrationService {

  //Uses alternate listing path
  @RequestMapping(value = "/{petId}", method = RequestMethod.GET)
  @ApiOperation(value = "Find pet by ID", notes = "Returns a pet when ID < 10. "
          + "ID > 10 or nonintegers will simulate API error conditions",
          response = Pet.class,
          extensions = {
            @Extension(properties = @ExtensionProperty(name="x-test1", value="value1")),
            @Extension(name="test2", properties = @ExtensionProperty(name="name2", value="value2"))
          }
  )
  public Pet getPetById(
          @ApiParam(value = "ID of pet that needs to be fetched", allowableValues = "range[1,5]", required = true)
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
    return new ResponseEntity<Example>(new Example("Hello", 1, EnumType.ONE, new NestedType("test")), HttpStatus.OK);
  }

  //Returns nested generic types
  @RequestMapping(value = "/effectives", method = RequestMethod.GET)
  private ResponseEntity<List<Example>> getEffectives() {
    return new ResponseEntity<List<Example>>(newArrayList(new Example("Hello", 1, EnumType.ONE,
            new NestedType("test"))),
            HttpStatus.OK);
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


  //Generic ollection input
  @RequestMapping(value = "/statuses", method = RequestMethod.POST)
  public void updateBazes(Collection<EnumType> enumType) {
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
  public ResponseEntity<Void> addFiles( @RequestPart("files") MultipartFile[] files) {
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
}
