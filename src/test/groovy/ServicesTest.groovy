import com.mangofactory.swagger.spring.controller.DocumentationController
import com.mangofactory.swagger.spring.test.TestConfiguration
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.server.test.context.WebContextLoader
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
// Spring imports omitted for brevity

@ContextConfiguration(loader = WebContextLoader.class, classes = TestConfiguration.class)
class ServicesTest extends Specification {
  @Autowired DocumentationController controller
  @Shared def apis
  @Shared def mockMvc

  def setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    apis = response(mockMvc, "/api-docs").apis
  }

  def "Number of services to be documented is 8"() {
    ResultActions actions

    given:
      MockHttpServletRequestBuilder requestBuilder =
        MockMvcRequestBuilders.get("/api-docs").accept(MediaType.APPLICATION_JSON);

    when:
      actions = mockMvc.perform(requestBuilder)

    then:
      actions.andExpect(status().isOk())
      def response = new JsonSlurper().parseText(actions.andReturn().response.contentAsString)
      response.apis.size == 8
  }

  @Unroll("#uri has #operations operations")
  def "Services are documented with the correct number of operations"() {
    ResultActions actions

    expect:
      def json = response(mockMvc, uri)
      json.apis.size == operations

    where:
      index   | uri           | operations
      1       | apis[0].path  | 1
      2       | apis[1].path  | 1
      3       | apis[2].path  | 3
      4       | apis[3].path  | 9
      5       | apis[4].path  | 1
      6       | apis[5].path  | 2
      7       | apis[6].path  | 2
      8       | apis[7].path  | 21
  }

  @Unroll("#uri has operation (#path) with params of type #withParams and returns #andReturns")
  def "Operations are documented correctly"() {
    ResultActions actions

    expect:
    def json = response(mockMvc, uri)
    def api = json.apis[index]
    def operation = api.operations[opIndex]
    def parameters = operation.parameters

    api.path == path
    operation.responseClass == andReturns
    if (parameters != null) {
      withParams.size() == parameters.size
      withParams.eachWithIndex { entry, i -> assert parameters[i].dataType == entry[0] && parameters[i].name == entry[1] }
    } else {
      withParams.size() == 0
    }

//    json.apis[index].operations[opIndex].parameters eachWithIndex { p, i ->
//      p.dataType == withParams[i][0] && p.name == withParams[i][1]  }

    where:
    index|uri|opIndex|path|andReturns|withParams
    0| apis[0].path| 0| "/businesses/{businessId}"| "String"| [["String", "businessId"]]

    0| apis[1].path| 0| "/no-request-mapping"| "ResponseEntity«Example»"| [["UriComponentsBuilder", "builder"]]

    0| apis[2].path| 0| "/fancypets/?{someId}"| "int"| [["FancyPet", "object"], ["int", "someId"]]
    1| apis[2].path| 0| "/fancypets"| "Void"| [["Pet«any»", "pet"]]
    2| apis[2].path| 0| "/fancypets"| "int"| [["FancyPet", "object"]]

    0| apis[3].path| 0| "/features/bigDecimal"| "Void"| [["BigDecimal", "input"]]
    1| apis[3].path| 0| "/features/date"| "Void"| [["LocalDate", "localDate"]]
    2| apis[3].path| 0| "/features/{petId}"| "Pet"| [["String", "petId"]]
    3| apis[3].path| 0| "/features/allMethodsAllowed"| "Void"| []
    4| apis[3].path| 0| "/features/effective"| "ResponseEntity«Example»"| [["UriComponentsBuilder", "builder"]]
    5| apis[3].path| 0| "/features/effectives"| "ResponseEntity«List«Example»»"| []
    6| apis[3].path| 0| "/features/effective"| "Void"| [["Example", "example"]]
    7| apis[3].path| 0| "/features/status"| "Void"| [["EnumType", "enumType"]]
    8| apis[3].path| 0| "/features/statuses"| "Void"| [["Collection«EnumType»", "enumType"]]

    0| apis[4].path| 0| "/child/child-method"| "String"| [["String", "parameter"]]

    0| apis[5].path| 0| "/petgrooming"| "ResponseEntity«Boolean»"| [["String", "type"]]
    1| apis[5].path| 0| "/petgrooming/voidMethod/{input}"| "Void"| [["String", "input"]]

    0| apis[6].path| 0| "/pets/grooming"| "ResponseEntity«Boolean»"| [["String", "type"]]
    1| apis[6].path| 0| "/pets/grooming/voidMethod/{input}"| "Void"| [["String", "input"]]


    0| apis[7].path| 0| "/pets/grooming"| "ResponseEntity«Boolean»"| [["String", "type"]]
    1| apis[7].path| 0| "/pets"| "ResponseEntity«Boolean»"| [["String", "type"]]
    2| apis[7].path| 0| "/pets/grooming/voidMethod/{input}"| "Void"| [["String", "input"]]
    3| apis[7].path| 0| "/pets/voidMethod/{input}"| "Void"| [["String", "input"]]
    4| apis[7].path| 0| "/pets/{a}/{b}"| "ResponseEntity«Void»"| [["String", "a"], ["String", "b"]]
    5| apis[7].path| 0| "/pets"| "Void"| [["Pet", "pet"]]
    6| apis[7].path| 0| "/pets/{petId}"| "Pet"| [["String", "petId"]]
    7| apis[7].path| 0| "/pets"| "Void"| [["Pet", "pet"]]
    8| apis[7].path| 0| "/pets/findByStatus"| "Pet"| [["String", "status"]]
    9| apis[7].path| 0| "/pets/findByTags"| "Pet"| [["String", "tags"]]
    10| apis[7].path| 0| "/pets/siblings"| "List[Pet]"| [["Pet", "pet"]]
    11| apis[7].path| 0| "/pets"| "List[Pet]"| [["Pet", "pet"]]
    12| apis[7].path| 0| "/pets/{name}"| "HttpEntity«Pet»"| [["String", "name"]]
    13| apis[7].path| 0| "/pets/echo"| "List[Entry«String,Pet»]"| [["List[Entry«String,Pet»]", "someInput"]]
    14| apis[7].path| 0| "/pets/transformPetNameToPetMapToAny"| "any"| [["List[Entry«String,Pet»]", "someInput"]]
    15| apis[7].path| 0| "/pets/transformPetNameToPetMapToGenericOpenMap"| "any"| [["List[Entry«String,Pet»]", "someInput"]]
    16| apis[7].path| 0| "/pets/transformPetNameToPetMapToOpenMap"| "any"| [["List[Entry«String,Pet»]", "someInput"]]
    17| apis[7].path| 0| "/pets/nameToNickNamesMap"| "List[Entry«String,List«String»»]"| [["String", "input"]]
    18| apis[7].path| 0| "/pets/byName/{name}"| "HttpEntity«List«Pet»»"| [["String", "name"]]
    19| apis[7].path| 0| "/pets/{petId}/pic"| "ResponseEntity«Void»"| [["String", "petId"]]
    20| apis[7].path| 0| "/pets/{petId}/pic/{picId}"| "ResponseEntity«Void»"| [["String", "petId"], ["String",
            "picId"]]

//    apis[7].path  | "/businesses/{businessId}" | withParams           | andReturns
  }

  def response(MockMvc mockMvc, String path) {
    def requestBuilder = MockMvcRequestBuilders.get(path).accept(MediaType.APPLICATION_JSON);
    def actions = mockMvc.perform(requestBuilder)
    new JsonSlurper().parseText(actions.andReturn().response.contentAsString)
  }


}