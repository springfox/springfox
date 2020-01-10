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
package springfox.test.contract.swagger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import springfox.test.contract.swagger.models.Bug1749;
import springfox.test.contract.swagger.models.EHDTOApplicatorUnits;
import springfox.test.contract.swagger.models.EnumType;
import springfox.test.contract.swagger.models.Example;
import springfox.test.contract.swagger.models.LanguageResponse;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.Response;
import java.beans.ConstructorProperties;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Api(tags = "Bugs")
@RestController
@RequestMapping("/bugs")
public class BugsController {
  @RequestMapping(value = "1306", method = POST)
  public ResponseEntity<Map<String, String>> bug1306(@RequestParam Map<String, String> paramMap) {
    return ResponseEntity.ok(null);
  }

  @ApiImplicitParams(
      @ApiImplicitParam(dataType = "string", allowMultiple = true, paramType = "header")
  )
  @RequestMapping(value = "1209", method = POST)
  public ResponseEntity<String> bug1209() {
    return ResponseEntity.ok("");
  }

  @RequestMapping(value = "1162", method = POST)
  public ResponseEntity<Date> bug1162() {
    return ResponseEntity.ok(new Date(new java.util.Date().getTime()));
  }

  @RequestMapping(value = "1376-bare", method = POST)
  public URL issue1376Bare() throws MalformedURLException {
    return new URL("http://example.org");
  }

  @RequestMapping(value = "1376-property", method = POST)
  public Bug1376 issue1376Property() throws MalformedURLException {
    return new Bug1376(new URL("http://example.org"));
  }

  @RequestMapping(value = "1376-input-bare", method = POST)
  public void issue1376Input(URL url) throws MalformedURLException {
  }

  @RequestMapping(value = "1376-input-property", method = POST)
  public void issue1376Input(Bug1376 bug) throws MalformedURLException {
  }

  @RequestMapping(value = "1420", method = GET)
  @ApiOperation(tags = { "foo" }, value = "issue1420")
  public String issue1420() {
    return "1420";
  }

  @RequestMapping(value = "1440", method = GET)
  public EntityModel<String> issue1440() {
    return new EntityModel<String>("1420");
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

  @RequestMapping(value = "2081", method = GET)
  public void bug2081(Bug2081 criteria) {
    throw new UnsupportedOperationException();
  }

  @ApiOperation(value = "Remove an apple from a user", notes = "Remove an apple from a user. You must specify the "
      + "user name and the apple name.", response = Void.class, consumes = "application/json, application/xml",
                produces = "application/json, application/xml")
  @ApiResponses({ @ApiResponse(code = 200, message = "The apple is removed") })
  @RequestMapping(value = "1722", method = POST)
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
  public ResponseEntity<Void> bug1778(
      TestClass testClass,
      TestClass2 testClass2) {
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

  @GetMapping(value = "/1841", produces = APPLICATION_ATOM_XML_VALUE)
  public void method1() {
  }

  @GetMapping(value = "/1841", produces = APPLICATION_JSON_UTF8_VALUE)
  public void method2() {
  }

  @RequestMapping(value = "/1939",
                  method = GET,
                  produces = "application/jwt")
  @ApiOperation(value = "authenticate a user using a given set of "
      + "credentials, producing a JWT token that may be "
      + "used for future API operations if successful")
  @Valid
  public ResponseEntity<String>
  authenticate(
      @RequestParam("username")
          String username,
      @RequestParam("password")
          String password,
      @RequestParam(required = false, name = "credential-source-id")
          String credentialSourceID) {
    return ResponseEntity.ok("Success!");
  }

  @GetMapping(value = "/1907", produces = APPLICATION_XML_VALUE)
  public void xmlPayload(@RequestBody Model1907 xml) {
  }

  @RequestMapping(path = "/2114", method = PUT)
  ResponseEntity<Void> bug2114(
      @PathVariable(value = "siteId") UUID siteId,
      @RequestParam(value = "siteSecret") UUID siteSecret,
      @RequestParam(value = "xmlUrl") URI xmlUrl,
      @RequestParam(value = "stripHtmlTags", required = false, defaultValue = "false") Boolean stripHtmlTags,
      @RequestParam(value = "clearIndex", required = false, defaultValue = "false") Boolean clearIndex
                              ) {
    return null;
  }

  @RequestMapping(value = "/2118", method = GET)
  public String bug2118(@RequestBody @ModelAttribute Example person) {
    return "ok";
  }

  @RequestMapping(method = GET, path = "{propertyKey}/{environmentKey}")
  public ResponseEntity<String> getProperty(
      @ApiParam(name = "propertyKey", value = "Key of the property", required = true)
      @PathVariable("propertyKey") Key propertyKey,
      @ApiParam(name = "environmentKey", value = "Key of the environment", required = false)
      @PathVariable("environmentKey") Key environmentKey
                                           ) {
    return ResponseEntity.ok("");
  }

  @ApiOperation(value = "2107")
  @GetMapping(value = "/2107/{someId}", produces = APPLICATION_JSON_VALUE)
  public String getSomeById(
      @ApiParam(value = "This is the description", defaultValue = "1f1f1f", required = true, name = "someId", type =
          "java.lang.String")
      @PathVariable("someId") Id someId) {
    return "";
  }

  @RequestMapping(value = "/1894", method = POST)
  public void cacheEvict1() {

  }

  @RequestMapping(value = "/1894", method = POST, consumes = APPLICATION_FORM_URLENCODED_VALUE)
  public void cacheEvict2() {

  }

  @PostMapping(value = "/1887/{env}/{list-id}/emails",
               produces = APPLICATION_JSON_UTF8_VALUE,
               consumes = APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "1887 example", response = Example.class)
  public ResponseEntity<Map<String, List<Example>>> addEmailsToList(
      @PathVariable String env,
      @PathVariable("list-id") String listId,
      @RequestBody List<String> emails) {
    return ResponseEntity.ok(null);
  }

  @PostMapping(path = "/1965-form-data", consumes = "multipart/form-data")
  public ResponseEntity<Example> bug1965FormData(Example sfData) {
    return ResponseEntity.ok(null);
  }

  @PostMapping(path = "/1965", consumes = "multipart/form-data")
  public ResponseEntity<Example> bug1965(
      @Valid @RequestPart(name = "sfParamMap") @RequestParam Map<String, String> paramMap,
      @Valid @RequestPart(name = "sfId") @RequestParam Integer sfId,
      @Valid @RequestPart(name = "sfData") Example sfData,
      @RequestParam(name = "file", required = false) MultipartFile supportFile) {
    return ResponseEntity.ok(null);
  }

  @GetMapping("/1926/filtered")
  public Lang filtered(@RequestBody LangNotFilteredWrapper wrapper) {
    return null;
  }

  @GetMapping("/1926/not-filtered")
  public Lang notFiltered(@RequestBody LangFilteredWrapper wrapper) {
    return null;
  }

  @ApiOperation(value = "测试RequesetParam", notes = "测试RequesetParam")
  @ApiImplicitParams({
                         @ApiImplicitParam(name = "date",
                                           value = "日期：2017-09-01",
                                           required = true,
                                           dataType = "String",
                                           paramType =
                                               "path"),
                         @ApiImplicitParam(name = "name", value = "名称", required = false, dataType = "string")
                     })
  @GetMapping("/2029")
  public String bug2020(
      @RequestParam(required = true, value = "date") String date,
      @RequestParam(required = false, value = "name") String name) {
    return date + name;
  }

  @GetMapping(path = "/{bar}/2148")
  @ApiImplicitParam(name = "bar", dataType = "long", value = "example")
  ResponseEntity<Example> bug2148(
      @ApiIgnore @PathVariable("bar") Example example,
      @RequestParam("year") Optional<Integer> year) {

    return ResponseEntity.notFound().build();
  }

  @GetMapping(path = "/2161")
  ResponseEntity<String> bug2161And2249and2469(@RequestBody Status status) {
    return ResponseEntity.ok("");
  }

  @GetMapping(path = "/1881")
  ResponseEntity<String> bug1881(@RequestBody Bug1881 container) {
    return ResponseEntity.ok("");
  }

  @ApiOperation(value = "Get all examples", nickname = "bug2268", notes = "Get all examples ", response = Example.class,
                responseContainer = "List", authorizations = {
      @Authorization(value = "user_auth", scopes = {
          @AuthorizationScope(scope = "ADMIN", description = "Manage users"),
          @AuthorizationScope(scope = "USER", description = "Maintain own user")
      })
  }, tags = { "example" })
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Success", response = Example.class, responseContainer = "List"),
  })
  @RequestMapping(value = "/2268",
                  produces = { "application/json" },
                  method = RequestMethod.GET)
  ResponseEntity<List<Example>> bug2268(
      @ApiParam(value = "Filter the list")
      @Valid
      @RequestParam(value = "$filter", required = false) String filter) {
    return null;
  }

  @RequestMapping(value = "/bug2203", method = RequestMethod.GET,
                  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Response<LanguageResponse>> bug2203() {
    return ResponseEntity.ok(null);
  }

  @GetMapping("/bug1827")
  public String addBook(
      @ModelAttribute Book book,
      @RequestParam(required = false) String[] authorIds) {
    return "";
  }

  @GetMapping("/bug2282")
  @ApiOperation("/bug2282")
  public String bug2282(User user) {
    return "";
  }

  @PostMapping(value = "/bug2230", consumes = MediaType.APPLICATION_ATOM_XML_VALUE)
  @ApiOperation("/bug2230")
  public String bug2230(
      @RequestBody EHDTOApplicatorUnits applicatorUnits) {
    return "";
  }

  @GetMapping(value = "/bug2182")
  @ApiOperation("/bug2182")
  public ProductVO bug2182() {
    return null;
  }

  @GetMapping({ "/bug2220", "/bug2220/{bar}" })
  public void bug2220(@PathVariable(value = "bar", required = false) String bar) {
  }

  @ApiResponses({
                    @ApiResponse(code = 404, message = "No object was found with the given ID"),
                    @ApiResponse(code = 200, message = "The object was deleted successfully.",
                                 response = void.class)
                })
  @GetMapping("/bug1944")
  public void bug1944() {
  }

  @PostMapping("/2378")
  public void upperCaseField(@RequestBody UpperCasedField input) {
  }

  @PostMapping("/2391")
  public void bug2391(@ModelAttribute Bug2391 input) {
  }

  @RequestMapping(value = "/2368", method = RequestMethod.GET)
  public ResponseEntity<Void> bug2368(@ModelAttribute @Valid GenericRequest<Void> voidRequest) {
    return ResponseEntity.ok(null);
  }

  @PostMapping("/2479")
  public void bug2479(@RequestBody Bug2479 input) {
  }


  @PostMapping("/2415")
  public void bug2415(@RequestBody Bug2415 input) {
  }

  @GetMapping("/2415")
  public ResponseEntity<String> bug2415(
      @Pattern(regexp = "^[A-Za-z0-9]{8,16}$")
      @Size(min = 8, max = 16)
      @RequestParam String input) {
    return ResponseEntity.ok("test");
  }

  @GetMapping("/2423")
  public void bug2423(Bug2423 input) {
  }

  @PostMapping("/2822")
  public void bug2822(@ApiParam(example = "exampleMessage") @RequestBody String message) {
    // Empty body is sufficient for testing
  }

  @SuppressWarnings("VisibilityModifier")
  public class Bug2423 {
    public String from;
    public String to;
  }

  public class Bug2415 {
    private String test;

    @Pattern(regexp = "^[A-Za-z0-9]{8,16}$")
    @Size(min = 8, max = 16)
    public String getTest() {
      return test;
    }

    public void setTest(String test) {
      this.test = test;
    }
  }

  public class GenericRequest<T> {

    @NotNull
    private T parameters;

    public T getParameters() {
      return parameters;
    }

    public void setParameters(T parameters) {
      this.parameters = parameters;
    }

  }

  public class ProductVO {
    private String name;

    @JsonUnwrapped(prefix = "specification_")
    private Specification specification;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Specification getSpecification() {
      return specification;
    }

    public void setSpecification(Specification specification) {
      this.specification = specification;
    }
  }

  public class Specification {
    private String name;
    @JsonUnwrapped(prefix = "child_")
    private SpecificationChild child;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public SpecificationChild getChild() {
      return child;
    }

    public void setChild(SpecificationChild child) {
      this.child = child;
    }
  }

  public class SpecificationChild {
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public class User {
    private Office office;

    public Office getOffice() {
      return office;
    }

    public void setOffice(Office office) {
      this.office = office;
    }
  }

  public class Office extends TreeEntity<Office> {
  }

  public class TreeEntity<T> {
    //    private T  parent ;
    private User user;

    public User getUser() {
      return user;
    }

    public void setUser(User user) {
      this.user = user;
    }
  }

  public class Book {
    private Long id;
    private String name;
    private Set<Author> authors;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Set<Author> getAuthors() {
      return authors;
    }

    public void setAuthors(Set<Author> authors) {
      this.authors = authors;
    }
  }

  public class Author {
    private Long id;
    private String name;
    private List<Book> books;

    public Long getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public List<Book> getBooks() {
      return books;
    }
  }

  public enum Lang {
    zh, en
  }

  @XmlType(name = "model1907", namespace = "urn:bugs")
  public static class Model1907 {

    @NotNull
    @XmlAttribute
    private String somename;
    @NotNull
    @XmlElement
    private Example example;

    public Model1907() {
    }

    public String getSomename() {
      return somename;
    }

    public void setSomename(String somename) {
      this.somename = somename;
    }

    public Example getExample() {
      return example;
    }

    public void setExample(Example example) {
      this.example = example;
    }
  }

  public static class Bug2081Filter {
    private String importantField;

    public String getImportantField() {
      return importantField;
    }

    public void setImportantField(String importantField) {
      this.importantField = importantField;
    }
  }

  public static class Bug2081 {
    private Bug2081Filter a;
    private Bug2081Filter b;

    public Bug2081Filter getA() {
      return a;
    }

    public void setA(Bug2081Filter a) {
      this.a = a;
    }

    public Bug2081Filter getB() {
      return b;
    }

    public void setB(Bug2081Filter b) {
      this.b = b;
    }
  }

  public static class Bug1881 {
    private Map<String, List> data1;
    private Map<String, List<Example>> data2;

    public Map<String, List> getData1() {
      return data1;
    }

    public void setData1(Map<String, List> data1) {
      this.data1 = data1;
    }

    public Map<String, List<Example>> getData2() {
      return data2;
    }

    public void setData2(Map<String, List<Example>> data2) {
      this.data2 = data2;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public class Status {
    @ApiModelProperty(example = "false")
    private final Boolean enabled;
    @ApiModelProperty(example = "'1235'")
    private final String integerString;
    @ApiModelProperty(example = "'[test] n/a'")
    private final String bug2469;

    @JsonProperty("bug_1964")
    @ApiModelProperty(required = true)
    private boolean bug1964;

    @JsonCreator
    Status(
        @JsonProperty("enabled") Boolean enabled,
        @JsonProperty("integerString") String integerString,
        @JsonProperty("bug2469") String bug2469) {
      this.enabled = enabled;
      this.integerString = integerString;
      this.bug2469 = bug2469;
    }

    @JsonProperty("enabled")
    public Boolean isEnabled() {
      return enabled;
    }

    @JsonProperty("integerString")
    public String getIntegerString() {
      return integerString;
    }

    @JsonProperty("bug_1964")
    @ApiModelProperty(required = true)
    public boolean isBug1964() {
      return bug1964;
    }

    public String getBug2469() {
      return bug2469;
    }
  }

  public class LangNotFilteredWrapper {
    private Lang lang;

    public LangNotFilteredWrapper(Lang lang) {
      this.lang = lang;
    }

    public Lang getLang() {
      return lang;
    }
  }

  public class LangFilteredWrapper {
    private Lang lang;

    @ConstructorProperties({ "lang" })
    public LangFilteredWrapper(Lang lang) {
      this.lang = lang;
    }

    public Lang getLang() {
      return lang;
    }
  }

  public class Id {

    private final Long id;

    public Id(Long id) {
      this.id = id;
    }

    public Long getId() {
      return id;
    }
  }

  public class Key {

    // if enabled, name will be shown @ApiModelProperty(value = "my description")
    private final String key;

    @JsonCreator
    public Key(@JsonProperty("key") String keyContent) {
      key = keyContent;
    }

    public String getKey() {
      return key;
    }
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
    private ByteBuffer bar;

    public ByteBuffer getBar() {
      return bar;
    }

    public void setBar(ByteBuffer bar) {
      this.bar = bar;
    }
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

    private URL url;

    Bug1376(URL url) {
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

  private class UpperCasedField {
    @ApiModelProperty(name = "AGE", value = "the age of person")
    @SuppressWarnings("MemberName")
    private Integer AGE;

    @SuppressWarnings({ "MemberName", "VisibilityModifier" })
    public Integer YEAR;

    public Integer getAGE() {
      return AGE;
    }

    @SuppressWarnings("ParameterName")
    public void setAGE(Integer AGE) {
      this.AGE = AGE;
    }
  }

  public class Bug2391 {
    @ApiModelProperty(name = "from_country_id", position = 1, required = true)
    private Long fromCountryId;

    @ModelAttribute("from_country_id")
    public Long getFromCountryId() {
      return fromCountryId;
    }

    public void setFromCountryId(Long fromCountryId) {
      this.fromCountryId = fromCountryId;
    }
  }

  private class Bug2479 {
    @ApiModelProperty("First")
    private Example first;

    @ApiModelProperty("Second")
    private Example second;

    public Example getFirst() {
      return first;
    }

    public void setFirst(Example first) {
      this.first = first;
    }

    public Example getSecond() {
      return second;
    }

    public void setSecond(Example second) {
      this.second = second;
    }
  }
}
