/*
 *
 *  Copyright 2015-2017 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.dummy.models.Bug1749;
import springfox.documentation.spring.web.dummy.models.EnumType;
import springfox.documentation.spring.web.dummy.models.Example;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Api(tags = "Bugs")
@RestController
@RequestMapping("/bugs")
public class BugsController {
  @RequestMapping(value = "1306", method = RequestMethod.POST)
  public ResponseEntity<Map<String, String>> bug1306(@RequestParam Map<String, String> paramMap) {
    return ResponseEntity.ok(null);
  }

  @ApiImplicitParams(
      @ApiImplicitParam(dataType = "string", allowMultiple = true, paramType = "header")
  )
  @RequestMapping(value = "1209", method = RequestMethod.POST)
  public ResponseEntity<String> bug1209() {
    return ResponseEntity.ok("");
  }

  @RequestMapping(value = "1162", method = RequestMethod.POST)
  public ResponseEntity<Date> bug1162() {
    return ResponseEntity.ok(new Date(new java.util.Date().getTime()));
  }

  @RequestMapping(value = "1376-bare", method = RequestMethod.POST)
  public URL issue1376Bare() throws MalformedURLException {
    return new URL("http://example.org");
  }

  @RequestMapping(value = "1376-property", method = RequestMethod.POST)
  public Bug1376 issue1376Property() throws MalformedURLException {
    return new Bug1376(new URL("http://example.org"));
  }

  @RequestMapping(value = "1376-input-bare", method = RequestMethod.POST)
  public void issue1376Input(URL url) throws MalformedURLException {
  }

  @RequestMapping(value = "1376-input-property", method = RequestMethod.POST)
  public void issue1376Input(Bug1376 bug) throws MalformedURLException {
  }

  @RequestMapping(value = "1420", method = GET)
  @ApiOperation(tags = { "foo" }, value = "issue1420")
  public String issue1420() {
    return "1420";
  }

  @RequestMapping(value = "1440", method = GET)
  public Resource<String> issue1440() {
    return new Resource<String>("1420");
  }

  @RequestMapping(value = "1475", method = GET)
  public Map<String, List<String>> mapOfLists() {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "1475-example", method = GET)
  public Map<String, List<Example>> mapOfListOfExample() {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "1605", method = GET)
  public byte[] byteArrayResponse() {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "1676", method = GET)
  public void apiModelProperty(@RequestBody Bug1676 value) {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "1632", method = GET)
  public void fileCustomType(@RequestBody File value) {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "1632s", method = GET)
  public void filesCustomType(@RequestBody List<File> values) {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "1697", method = GET)
  public void payloadWithByteBuffer(@RequestBody Bug1697 body) {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "1627", method = GET)
  public void bug1627(@RequestBody Bug1627 body) {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "Remove an apple from a user", notes = "Remove an apple from a user. You must specify the "
      + "user name and the apple name.", response = Void.class, consumes = "application/json, application/xml",
      produces = "application/json, application/xml")
  @ApiResponses({ @ApiResponse(code = 200, message = "The apple is removed") })
  @RequestMapping(value = "1722", method = RequestMethod.POST)
  public void bug1722(@RequestBody String test) {
  }

  @RequestMapping(value = "1734", method = GET)
  public void bug1734(
      @ApiParam(name = "offset", value = "The value of offset", defaultValue = "0")
      @RequestParam(value = "offset", defaultValue = "0", required = false)
          int offset) {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "1740", method = GET)
  public Bug1740 bug1740() {
    return new Bug1740();
  }

  @ApiResponses(value = {
      @ApiResponse(code = 200,
          message = "list of ids",
          response = String.class),
      @ApiResponse(code = 204,
          message = "no ids found",
          response = Void.class)
  })
  @RequestMapping(value = "/1750a", method = GET)
  public ResponseEntity<String> bug1750a() {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "1750b", response = String.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200,
          message = "list of ids",
          response = String.class),
      @ApiResponse(code = 204,
          message = "no ids found",
          response = Void.class)
  })
  @RequestMapping(value = "/1750b", method = GET)
  public ResponseEntity<String> bug1750b() {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "/1777", method = GET)
  public ResponseEntity<Bug1777> bug1777() {
    throw new UnsupportedOperationException();
  }


  @RequestMapping(value = "/1778", method = GET)
  public ResponseEntity<Void> bug1778(TestClass testClass, TestClass2 testClass2) {
    return ResponseEntity.ok().build();
  }

  @ApiOperation(value = "Retrieve all the companies")
  @RequestMapping(value = "/1749", method = GET)
  public List<String> getAllPaged(
      @Valid Bug1749 request,
      HttpServletResponse response,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String companyName,
      @RequestParam(required = false) Boolean like) throws Exception {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "/1819a", method = PUT)
  public void modelWithListOfEnumsAsJson(@RequestBody Model1819 model) {
  }

  @RequestMapping(value = "/1819b", method = POST)
  public void modelWithListOfEnumsAsModelAttribute(@ModelAttribute Model1819 model) {
  }

  @GetMapping("/1864")
  public void test(@Valid Model1864 req) {

  }

  @GetMapping(value = "/1841", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
  public void method1() {
  }

  @GetMapping(value = "/1841", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public void method2() {
  }

  public class Model1864 {
    @NotNull
    private String somename;

    public String getSomename() {
      return somename;
    }

    public void setSomename(String somename) {
      this.somename = somename;
    }
  }

  public class Model1819 {

    private List<EnumType> enumTypes;

    public List<EnumType> getEnumTypes() {
      return enumTypes;
    }

    public void setEnumTypes(List<EnumType> enumTypes) {
      this.enumTypes = enumTypes;
    }
  }

  class TestClass {

    private String s;

    public String getS() {
      return s;
    }

    public void setS(String s) {
      this.s = s;
    }
  }

  class TestClass2 {

    private String e;

    public String getE() {
      return e;
    }

    public void setE(String e) {
      this.e = e;
    }
  }

  @ApiModel(description = "Test 1777")
  public class Bug1777 {
    @ApiModelProperty(value = "经度", required = true)
    private Double longitude;

    public Double getLongitude() {
      return longitude;
    }

    public void setLongitude(Double longitude) {
      this.longitude = longitude;
    }
  }

  public class Bug1627 {
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  class Bug1697 {
    public ByteBuffer getBar() {
      return bar;
    }

    public void setBar(ByteBuffer bar) {
      this.bar = bar;
    }

    private ByteBuffer bar;
  }


  class File {
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  class Bug1676 {
    @ApiModelProperty(value = "Horizontal position", required = false, example = "200")
    private float xAxis;
    @ApiModelProperty(value = "Vertical position", required = false, example = "500")
    private float yAxis;

    public float getxAxis() {
      return xAxis;
    }

    public void setxAxis(float xAxis) {
      this.xAxis = xAxis;
    }

    public float getyAxis() {
      return yAxis;
    }

    public void setyAxis(float yAxis) {
      this.yAxis = yAxis;
    }
  }

  class Bug1376 {

    URL url;

    public Bug1376(URL url) {
      this.url = url;
    }

    public URL getUrl() {
      return url;
    }

    public void setUrl(URL url) {
      this.url = url;
    }

  }

  public class LinkAlternate {
    private String href;

    public String getHref() {
      return href;
    }

    public void setHref(String href) {
      this.href = href;
    }
  }

  public class Bug1740 {
    private String value;

    @JsonUnwrapped
    private Bug1740Inner inner;

    public String getValue() {
      return value;
    }

    public Bug1740Inner getInner() {
      return inner;
    }

    private final class Bug1740Inner {
      private String innerValue;

      public String getInnerValue() {
        return innerValue;
      }
    }
  }
}
