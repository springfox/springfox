package springdox.documentation.spring.web.readers

import com.fasterxml.classmate.TypeResolver
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.method.HandlerMethod
import spock.lang.Ignore
import springdox.documentation.schema.Model
import springdox.documentation.schema.ModelProperty
import springdox.documentation.spi.service.contexts.RequestMappingContext
import springdox.documentation.spring.web.dummy.DummyModels
import springdox.documentation.spring.web.dummy.controllers.BusinessService
import springdox.documentation.spring.web.dummy.controllers.PetService
import springdox.documentation.spring.web.dummy.models.FoobarDto
import springdox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.mixins.ServicePluginsSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec
import springdox.documentation.spring.web.plugins.DocumentationPluginsManager
import springdox.documentation.spring.web.scanners.ApiModelReader

import javax.servlet.http.HttpServletResponse

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport, ServicePluginsSupport])
class ApiModelReaderSpec extends DocumentationContextSpec {

  ApiModelReader sut
  DocumentationPluginsManager pluginsManager

  def setup() {
    pluginsManager = defaultWebPlugins()
    sut = new ApiModelReader(modelProvider(), new TypeResolver(), pluginsManager)
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
      model.getQualifiedType() == 'springdox.documentation.spring.web.dummy.DummyModels$BusinessModel'

      Map<String, ModelProperty> modelProperties = model.getProperties()
      modelProperties.size() == 2

      ModelProperty nameProp = modelProperties['name']
      nameProp.type.erasedType == String
      nameProp.getQualifiedType() == 'java.lang.String'
      nameProp.getPosition() == 0
      !nameProp.isRequired()
      nameProp.getDescription() == null
//      "${nameProp.allowableValues().getClass()}".contains('com.wordnik.swagger.model.AnyAllowableValues')
      def item = nameProp.getModelRef()
      item.type == "string"
      !item.collection
      item.itemType == null

      //TODO test these remaining
//      println model.description()
//      println model.baseModel()
//      println model.discriminator()
//      println model.subTypes()
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

  @Ignore("This needs to move to a swagger 1.2 test")
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
      plugin
              .genericModelSubstitutes(ResponseEntity, HttpEntity)
              .configure(contextBuilder)

    and:
      def pluginContext =  contextBuilder.build()
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
              new TypeResolver(), defaultWebPlugins())
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
