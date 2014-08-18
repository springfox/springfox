package com.mangofactory.swagger.integration
import com.google.common.base.Charsets
import com.mangofactory.swagger.core.Resources
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.server.test.context.WebContextLoader
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.yaml.snakeyaml.Yaml
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer

import static com.google.common.collect.Maps.newHashMap
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebAppConfiguration
@ContextConfiguration(loader = WebContextLoader, classes = ServicesConfiguration)
public class ServicesIntegrationTest extends Specification {
  @Autowired private WebApplicationContext context;
  @Shared def testCases = fromYaml()
  def mockMvc
  def apis

  def setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    apis = response(mockMvc, "/api-docs").apis
  }

   def "Number of services to be documented is 8"() {
      given:
        ResultActions actions
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/api-docs").accept(MediaType.APPLICATION_JSON);

      when:
        actions = mockMvc.perform(requestBuilder)

      then:
        actions.andExpect(status().isOk())
        def bytes = ByteBuffer.wrap(actions.andReturn().response.contentAsByteArray)
        def decoded = Charsets.UTF_8.decode(bytes)
        def response = new JsonSlurper().parseText(decoded.toString())
        response.apis.size == 9
   }

   @Unroll("#parentUri has #operations operations")
   def "Services are documented with the correct number of operations"() {
      expect:
        def documentationUri = parentUri
        def api = apis.find { it.path ==  documentationUri }
        api != null
        def apiListing = response(mockMvc, "/api-docs" + api.path)

        operations == apiListing.apis.size

      where:
        entry << countsByOperation(testCases)
        parentUri = entry.getKey()
        operations = entry.getValue()
   }

   @Unroll("##index #expectedUri - #testDescription")
   def "Operations are documented correctly"() {
      given:
        def json = response(mockMvc, "/api-docs" + documentationUri)

      when:
        def api = findApi(json, expectedUri, httpMethod, expectedParams, returnType)

      then:
        assert api != null
        def operation = api.operations[0]
        def actualParams = operation.parameters
        if (actualParams != null && expectedParams != null) {
           expectedParams.size == actualParams.size
           expectedParams.eachWithIndex { entry, i ->
              def parameter = entry
              assert actualParams[i].type == parameter.get("type") && actualParams[i].name == parameter.get("name")
           }
        } else {
           expectedParams == null && (actualParams == null || actualParams.size == 0)
        }

      where:
        record << fromYaml()
        index = record.get("index")
        expectedUri = record.get("expectedUri")
        documentationUri = record.get("parentUri")
        returnType = record.get("returnType")
        expectedParams = record.get("parameters")
        testDescription = record.get("testDescription")
        httpMethod = record.get("httpMethod")
   }

  def findApi(Map<String, Object> json, def expectedUri, def httpMethod, def expectedParams, def returnType) {
    def operationUri = expectedUri
    def method = httpMethod == null ? null : httpMethod
    def params = expectedParams
    def returnClass = returnType
    def found = json.apis.find { candidate ->
      (candidate.path == operationUri &&
              (method == null || candidate.operations[0].get("method") == method) &&
              paramSize(params) == paramSize(candidate.operations[0].parameters) &&
              returnParameterMatches(returnClass, candidate))
    }
    found
  }

  private boolean returnParameterMatches(def returnClass, def  candidate) {
    if (candidate.operations[0].type == "array") {
      candidate.operations[0].items.$ref == "Pet" //DK TODO : hard coded value as this is the only return type so far
    } else {
      returnClass == okMessage(candidate.operations[0].responseMessages)?.responseModel ||
            returnClass == candidate.operations[0].type
    }
  }

  def okMessage(def responseMessages) {
    responseMessages.find {
      it.code == 200
    }
  }

  def paramSize(def params) {
    return params == null ? 0 : params.size()
  }

  def response(MockMvc mockMvc, String path) {
    def requestBuilder = MockMvcRequestBuilders.get(path).accept(MediaType.APPLICATION_JSON);
    def actions = mockMvc.perform(requestBuilder) //.andDo(MockMvcResultHandlers.print())
    def reader = new InputStreamReader(new ByteArrayInputStream(actions.andReturn().response.getContentAsByteArray()))
    def yaml = new Yaml()
    yaml.load(reader) as Map<String, Object>
  }

  def fromYaml() {
    def testsYaml = Resources.load("/service-integration-use-cases.yaml")
    def reader = new StringReader(testsYaml)
    def yaml = new Yaml()
    Map<String, Object> recordMap = yaml.load(reader) as Map<String, Object>
    recordMap.testcases
  }

  def countsByOperation(testCases) {
    Map<Integer, Integer> counts = newHashMap()
    testCases.each { record ->
      String parentUri = record.get("parentUri")
      if (counts.containsKey(parentUri)) {
        counts.put(parentUri, counts.get(parentUri) + 1)
      } else {
        counts.put(parentUri, 1)
      }
    }
    counts.entrySet()
  }

}
