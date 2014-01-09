package com.mangofactory.swagger.readers

import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.wordnik.swagger.model.Model
import com.wordnik.swagger.model.ModelProperty
import spock.lang.Specification

import static com.mangofactory.swagger.ScalaUtils.*

@Mixin(RequestMappingSupport)
class ApiModelReaderSpec extends Specification {

   def "Method return type model"() {
    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), dummyHandlerMethod('methodWithConcreteResponseBody'))

    when:
      ApiModelReader apiModelReader = new ApiModelReader()
      apiModelReader.execute(context)
      Map<String, Object> result = context.getResult()

      Model model = result.get("model")
    then:
      true
      model.id == 'BusinessModel'
      model.name() == 'BusinessModel'
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

   def "void method"() {
    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), dummyHandlerMethod('methodWithHttpGETMethod'))

    when:
      ApiModelReader apiModelReader = new ApiModelReader()
      apiModelReader.execute(context)
      Map<String, Object> result = context.getResult()

      Model model = result.get("model")
    then:
     null == model
   }
}
