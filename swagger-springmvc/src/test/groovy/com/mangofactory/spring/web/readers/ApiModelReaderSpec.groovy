package com.mangofactory.spring.web.readers
import com.mangofactory.service.model.Model
import com.mangofactory.service.model.ModelProperty
import com.mangofactory.spring.web.plugins.DocumentationPluginsManager
import com.mangofactory.spring.web.scanners.RequestMappingContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.dummy.DummyModels
import com.mangofactory.swagger.dummy.controllers.BusinessService
import com.mangofactory.swagger.dummy.controllers.PetService
import com.mangofactory.swagger.dummy.models.FoobarDto
import com.mangofactory.swagger.mixins.ApiOperationSupport
import com.mangofactory.swagger.mixins.JsonSupport
import com.mangofactory.swagger.mixins.ModelProviderForServiceSupport
import com.mangofactory.swagger.mixins.PluginsSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.method.HandlerMethod

import javax.servlet.http.HttpServletResponse

@Mixin([RequestMappingSupport, ApiOperationSupport, JsonSupport, ModelProviderForServiceSupport, PluginsSupport])
class ApiModelReaderSpec extends DocumentationContextSpec {

  ApiModelReader sut
  DocumentationPluginsManager pluginsManager

  def setup() {
    pluginsManager = springPluginsManagerWithDefaults(defaultValues)
    sut = new ApiModelReader(modelProvider(), defaultValues.typeResolver, pluginsManager)
  }


  def "Method return type model"() {
    given:
      RequestMappingContext context = context(dummyHandlerMethod('methodWithConcreteResponseBody'))

    when:
      def models = sut.read(context)

    then:
      Model model = models['BusinessModel']
      model.id == 'BusinessModel'
      model.getName() == 'BusinessModel'
      model.getQualifiedType() == 'com.mangofactory.swagger.dummy.DummyModels$BusinessModel'

      Map<String, ModelProperty> modelProperties = model.getProperties()
      modelProperties.size() == 2

      ModelProperty nameProp = modelProperties['name']
      nameProp.typeName() == 'string'
      nameProp.getQualifiedType() == 'java.lang.String'
      nameProp.getPosition() == 0
      !nameProp.isRequired()
      nameProp.getDescription() == null
//      "${nameProp.allowableValues().getClass()}".contains('com.wordnik.swagger.model.AnyAllowableValues')
      nameProp.getItems() == null

      //TODO test these remaining
//      println model.description()
//      println model.baseModel()
//      println model.discriminator()
//      println model.subTypes()
  }

  def "Annotated model"() {
    given:
      RequestMappingContext context = context(dummyHandlerMethod('methodWithModelAnnotations'))
    when:
      def models = sut.read(context)

    then:
      Model model = models['AnnotatedBusinessModel']
      model.id == 'AnnotatedBusinessModel'
      model.getName() == 'AnnotatedBusinessModel'
      model.getQualifiedType() == 'com.mangofactory.swagger.dummy.DummyModels$AnnotatedBusinessModel'

      Map<String, ModelProperty> modelProps = model.getProperties()
      ModelProperty prop = modelProps.name
      prop.typeName() == 'string'
      prop.getDescription() == 'The name of this business'
      prop.isRequired()

      modelProps.numEmployees.getDescription() == 'Total number of current employees'
      !modelProps.numEmployees.isRequired()
  }

  def "Should pull models from Api Operation response class"() {
    given:

      RequestMappingContext context = context(dummyHandlerMethod('methodApiResponseClass'))
    when:
      def models = sut.read(context)

    then:
      models['FunkyBusiness'].getQualifiedType() == 'com.mangofactory.swagger.dummy.DummyModels$FunkyBusiness'
  }

  def "Should pull models from operation's ApiResponse annotations"() {
    given:

      RequestMappingContext context = context(dummyHandlerMethod('methodAnnotatedWithApiResponse'))
    when:
      def models = sut.read(context)

    then:
      models.size() == 2
      models['RestError'].getQualifiedType() == 'com.mangofactory.swagger.dummy.RestError'
      models['Void'].getQualifiedType() == 'java.lang.Void'
  }

  def context(HandlerMethod handlerMethod) {
    return new RequestMappingContext(context(), requestMappingInfo('/somePath'), handlerMethod)
  }

  def "should only generate models for request parameters that are annotated with Springs RequestBody"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodParameterWithRequestBodyAnnotation',
              DummyModels.BusinessModel,
              HttpServletResponse.class,
              DummyModels.AnnotatedBusinessModel.class
      )
      RequestMappingContext context = new RequestMappingContext(context(), requestMappingInfo('/somePath'),
              handlerMethod)
    when:
      def models = sut.read(context)

    then:
      models.size() == 2 // instead of 3
      models.containsKey("BusinessModel")
      models.containsKey("Void")

  }

  def "Generates the correct models when there is a Map object in the input parameter"() {
    given:
      HandlerMethod handlerMethod = handlerMethodIn(PetService, 'echo', Map)
      RequestMappingContext context = new RequestMappingContext(context(), requestMappingInfo('/echo'), handlerMethod)

    when:
      def models = sut.read(context)

    then:
      models.size() == 2
      models.containsKey("Entry«string,Pet»")
      models.containsKey("Pet")

  }

  def "Generates the correct models when alternateTypeProvider returns an ignoreable or base parameter type"() {
    given:
      def pluginContext = plugin
              .genericModelSubstitutes(ResponseEntity, HttpEntity)
              .build(contextBuilder)

    and:
      HandlerMethod handlerMethod = handlerMethodIn(BusinessService, 'getResponseEntity', String)
      RequestMappingContext context =
              new RequestMappingContext(pluginContext, requestMappingInfo('/businesses/responseEntity/{businessId}'),
                      handlerMethod )
    when:
      def models = sut.read(context)

    then:
      models.size() == 0

  }

  def "property description should be populated when type is used in response and request body"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSameAnnotatedModelInReturnAndRequestBodyParam',
              DummyModels.AnnotatedBusinessModel
      )
      RequestMappingContext context = new RequestMappingContext(context(), requestMappingInfo('/somePath'), handlerMethod)

    when:
      def models = sut.read(context)

    then:
      models.size() == 1

      String modelName = DummyModels.AnnotatedBusinessModel.class.simpleName
      models.containsKey(modelName)

      Model model = models[modelName]
      Map modelProperties = model.getProperties()
      modelProperties.containsKey('name')

      ModelProperty nameProperty = modelProperties['name']
      !nameProperty.getDescription().isEmpty()

  }

  def "model should include property that is only visible during serialization"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSerializeOnlyPropInReturnAndRequestBodyParam',
              DummyModels.ModelWithSerializeOnlyProperty
      )
      RequestMappingContext context = new RequestMappingContext(context(), requestMappingInfo('/somePath'),
              handlerMethod)

    when:
      def models = sut.read(context)

    then:
      models.size() == 1

      String modelName = DummyModels.ModelWithSerializeOnlyProperty.class.simpleName
      models.containsKey(modelName)

      Model model = models[modelName]
      Map modelProperties = model.getProperties()
      modelProperties.size() == 2
      modelProperties.containsKey('visibleForSerialize')
      modelProperties.containsKey('alwaysVisible')

  }


  def "model should include snake_case property that is only visible during serialization when objectMapper has CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSerializeOnlyPropInReturnAndRequestBodyParam',
              DummyModels.ModelWithSerializeOnlyProperty
      )
      RequestMappingContext context = new RequestMappingContext(context(), requestMappingInfo('/somePath'), handlerMethod)
    and:
      def snakeCaseReader = new ApiModelReader(modelProviderWithSnakeCaseNamingStrategy(),
              defaultValues.typeResolver, springPluginsManager())
    when:
      def models = snakeCaseReader.read(context)

    then:
      models.size() == 1

      String modelName = DummyModels.ModelWithSerializeOnlyProperty.class.simpleName
      models.containsKey(modelName)

      Model model = models[modelName]
      Map modelProperties = model.getProperties()
      modelProperties.size() == 2
      modelProperties.containsKey('visible_for_serialize')
      modelProperties.containsKey('always_visible')

  }

  def "Test to verify issue #283"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestFoobarDto', FoobarDto)
      RequestMappingContext context = new RequestMappingContext(context(), requestMappingInfo('/somePath'), handlerMethod)

    when:
      def models = sut.read(context)

    then:
      models.size() == 1

      String modelName = FoobarDto.simpleName
      models.containsKey(modelName)

      Model model = models[modelName]
      Map modelProperties = model.getProperties()
      modelProperties.containsKey('visibleForSerialize')

  }
}
