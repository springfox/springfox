package com.mangofactory.swagger.readers

import com.mangofactory.swagger.mixins.ApiOperationSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.wordnik.swagger.model.ApiDescription
import com.wordnik.swagger.model.Model
import com.wordnik.swagger.model.ModelProperty
import com.wordnik.swagger.model.Operation
import spock.lang.Ignore
import spock.lang.Specification

import static com.mangofactory.swagger.ScalaUtils.*

@Mixin([RequestMappingSupport, ApiOperationSupport])
class ApiModelReaderSpec extends Specification {

   def "Method return type model"() {
    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), dummyHandlerMethod('methodWithConcreteResponseBody'))
      List<Operation> operationList = Arrays.asList(operation('com.mangofactory.swagger.dummy.DummyModels$BusinessModel'))
      ApiDescription description = new ApiDescription(
              "anyPath",
              toOption("anyDescription"),
              toScalaList(operationList)
      )

      context.put("apiDescriptionList", [description])

    when:
      ApiModelReader apiModelReader = new ApiModelReader()
      apiModelReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      List<Model> models = result.get("models")
      Model model = models[0]
      model.id == 'com.mangofactory.swagger.dummy.DummyModels$BusinessModel'
      model.name() == 'com.mangofactory.swagger.dummy.DummyModels$BusinessModel'
      model.qualifiedType() == 'com.mangofactory.swagger.dummy.DummyModels$BusinessModel'

      Map<String, ModelProperty> modelProperties = fromScalaMap(model.properties())
      modelProperties.size() == 2

      ModelProperty nameProp = modelProperties['name']
      nameProp.type() == 'string'
      nameProp.qualifiedType() == 'java.lang.String'
      nameProp.position() == 0
      nameProp.required() == false
      nameProp.description() == toOption(null)
      "${nameProp.allowableValues().getClass()}".contains('com.wordnik.swagger.model.AnyAllowableValues')
      fromOption(nameProp.items()) == null

      //TODO test these remaining
//      println model.description()
//      println model.baseModel()
//      println model.discriminator()
//      println model.subTypes()
   }

   @Ignore
   def "void method"() {
    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), dummyHandlerMethod('methodWithHttpGETMethod'))

    when:
      ApiModelReader apiModelReader = new ApiModelReader()
      apiModelReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      List<Model> models = result.get("models")
      null == models[0]
   }

   def "Annotated model"() {
    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), dummyHandlerMethod('methodWithModelAnnotations'))
      List<Operation> operationList = Arrays.asList(operation('com.mangofactory.swagger.dummy.DummyModels$AnnotatedBusinessModel'))
      ApiDescription description = new ApiDescription(
              "anyPath",
              toOption("anyDescription"),
              toScalaList(operationList)
      )

      context.put("apiDescriptionList", [description])

    when:
      ApiModelReader apiModelReader = new ApiModelReader()
      apiModelReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      List<Model> models = result.get("models")
      Model model = models[0]
      model.id == 'com.mangofactory.swagger.dummy.DummyModels$AnnotatedBusinessModel'
      model.name() == 'com.mangofactory.swagger.dummy.DummyModels$AnnotatedBusinessModel'
      model.qualifiedType() == 'com.mangofactory.swagger.dummy.DummyModels$AnnotatedBusinessModel'

      Map<String, ModelProperty> modelProps = fromScalaMap(model.properties())
      ModelProperty prop = modelProps.name
      prop.type == 'string'
      fromOption(prop.description()) == 'The name of this business'
      prop.required() == true

      fromOption(modelProps.numEmployees.description()) == 'Total number of current employees'
      modelProps.numEmployees.required() == false

   }

   def "Should pull models from Api Operation response class"() {
    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), dummyHandlerMethod('methodApiResponseClass'))
      List<Operation> operationList = Arrays.asList(operation('com.mangofactory.swagger.dummy.DummyModels$FunkyBusiness'))
      ApiDescription description = new ApiDescription(
              "anyPath",
              toOption("anyDescription"),
              toScalaList(operationList)
      )

      context.put("apiDescriptionList", [description])

    when:
      ApiModelReader apiModelReader = new ApiModelReader()
      apiModelReader.execute(context)
      Map<String, Object> result = context.getResult()

      List<Model> model = result.get("models")
    then:
      model[0].qualifiedType() == 'com.mangofactory.swagger.dummy.DummyModels$FunkyBusiness'
   }
}
