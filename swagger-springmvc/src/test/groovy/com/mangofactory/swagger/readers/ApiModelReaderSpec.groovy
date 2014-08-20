package com.mangofactory.swagger.readers
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.dummy.DummyModels
import com.mangofactory.swagger.dummy.controllers.BusinessService
import com.mangofactory.swagger.dummy.controllers.PetService
import com.mangofactory.swagger.dummy.models.FoobarDto
import com.mangofactory.swagger.mixins.ApiOperationSupport
import com.mangofactory.swagger.mixins.JsonSupport
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.models.alternates.WildcardType
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.wordnik.swagger.model.ApiDescription
import com.wordnik.swagger.model.Model
import com.wordnik.swagger.model.ModelProperty
import com.wordnik.swagger.model.Operation
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.method.HandlerMethod
import spock.lang.Specification

import javax.servlet.http.HttpServletResponse

import static com.mangofactory.swagger.ScalaUtils.*
import static com.mangofactory.swagger.models.alternates.Alternates.newRule

@Mixin([RequestMappingSupport, ApiOperationSupport, JsonSupport, ModelProviderSupport])
class ApiModelReaderSpec extends Specification {

  def "Method return type model"() {
    given:
      RequestMappingContext context = contextWithApiDescription(dummyHandlerMethod('methodWithConcreteResponseBody'))
      SwaggerGlobalSettings settings = new SwaggerGlobalSettings()
      def modelConfig = new SwaggerModelsConfiguration()
      def typeResolver = new TypeResolver()
      settings.alternateTypeProvider = modelConfig.alternateTypeProvider(typeResolver)
      settings.ignorableParameterTypes = new SpringSwaggerConfig().defaultIgnorableParameterTypes()
      context.put("swaggerGlobalSettings", settings)
    when:
      ApiModelReader apiModelReader = new ApiModelReader(modelProvider())
      apiModelReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      Map<String, Model> models = result.get("models")
      Model model = models['BusinessModel']
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
//      "${nameProp.allowableValues().getClass()}".contains('com.wordnik.swagger.model.AnyAllowableValues')
      fromOption(nameProp.items()) == null



      //TODO test these remaining
//      println model.description()
//      println model.baseModel()
//      println model.discriminator()
//      println model.subTypes()
  }

  def "Annotated model"() {
    given:
      RequestMappingContext context = contextWithApiDescription(dummyHandlerMethod('methodWithModelAnnotations'))
      SwaggerGlobalSettings settings = new SwaggerGlobalSettings()
      def modelConfig = new SwaggerModelsConfiguration()
      def typeResolver = new TypeResolver()
      settings.alternateTypeProvider = modelConfig.alternateTypeProvider(typeResolver)
      settings.ignorableParameterTypes = new SpringSwaggerConfig().defaultIgnorableParameterTypes()
      context.put("swaggerGlobalSettings", settings)
    when:
      ApiModelReader apiModelReader = new ApiModelReader(modelProvider())
      apiModelReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      Map<String, Model> models = result.get("models")
      Model model = models['AnnotatedBusinessModel']
      model.id == 'AnnotatedBusinessModel'
      model.name() == 'AnnotatedBusinessModel'
      model.qualifiedType() == 'com.mangofactory.swagger.dummy.DummyModels$AnnotatedBusinessModel'

      Map<String, ModelProperty> modelProps = fromScalaMap(model.properties())
      ModelProperty prop = modelProps.name
      prop.type == 'string'
      fromOption(prop.description()) == 'The name of this business'
      prop.required() == true
      println swaggerCoreSerialize(model)

      fromOption(modelProps.numEmployees.description()) == 'Total number of current employees'
      modelProps.numEmployees.required() == false



  }

  def "Should pull models from Api Operation response class"() {
    given:

      RequestMappingContext context = contextWithApiDescription(dummyHandlerMethod('methodApiResponseClass'), null)
      SwaggerGlobalSettings settings = new SwaggerGlobalSettings()
      def modelConfig = new SwaggerModelsConfiguration()
      def typeResolver = new TypeResolver()
      settings.alternateTypeProvider = modelConfig.alternateTypeProvider(typeResolver)
      settings.ignorableParameterTypes = new SpringSwaggerConfig().defaultIgnorableParameterTypes()
      context.put("swaggerGlobalSettings", settings)
    when:
      ApiModelReader apiModelReader = new ApiModelReader(modelProvider())
      apiModelReader.execute(context)
      Map<String, Object> result = context.getResult()

      Map<String, Model> models = result.get("models")
    then:
      println models
      models['FunkyBusiness'].qualifiedType() == 'com.mangofactory.swagger.dummy.DummyModels$FunkyBusiness'
  }

  def contextWithApiDescription(HandlerMethod handlerMethod, List<Operation> operationList = null) {
    RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), handlerMethod)
    def scalaOpList = null == operationList ? emptyScalaList() : toScalaList(operationList)
    ApiDescription description = new ApiDescription(
          "anyPath",
          toOption("anyDescription"),
          scalaOpList
    )
    context.put("apiDescriptionList", [description])

    def settings = new SwaggerGlobalSettings()
    settings.ignorableParameterTypes = new SpringSwaggerConfig().defaultIgnorableParameterTypes();
    def modelConfig = new SwaggerModelsConfiguration()
    def typeResolver = new TypeResolver()
    settings.alternateTypeProvider = modelConfig.alternateTypeProvider(typeResolver)
    context.put("swaggerGlobalSettings", settings)
    context
  }

  def "should only generate models for request parameters that are annotated with Springs RequestBody"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodParameterWithRequestBodyAnnotation',
            DummyModels.BusinessModel,
            HttpServletResponse.class,
            DummyModels.AnnotatedBusinessModel.class
      )
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), handlerMethod)

      def settings = new SwaggerGlobalSettings()

      def config = new SpringSwaggerConfig()
      settings.ignorableParameterTypes = config.defaultIgnorableParameterTypes()
      def modelConfig = new SwaggerModelsConfiguration()
      def typeResolver = new TypeResolver()
      settings.alternateTypeProvider = modelConfig.alternateTypeProvider(typeResolver)
      context.put("swaggerGlobalSettings", settings)
    when:
      ApiModelReader apiModelReader = new ApiModelReader(modelProvider())
      apiModelReader.execute(context)
      Map<String, Object> result = context.getResult()
    then:
      Map<String, Model> models = result.get("models")
      models.size() == 2 // instead of 3
      models.containsKey("BusinessModel")
      models.containsKey("Void")

  }

  def "Generates the correct models when there is a Map object in the input parameter"() {
    given:
      HandlerMethod handlerMethod = handlerMethodIn(PetService, 'echo', Map)
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/echo'), handlerMethod)

      def settings = new SwaggerGlobalSettings()
      def config = new SpringSwaggerConfig()
      settings.ignorableParameterTypes = config.defaultIgnorableParameterTypes()
      def modelConfig = new SwaggerModelsConfiguration()
      def typeResolver = new TypeResolver()
      settings.alternateTypeProvider = modelConfig.alternateTypeProvider(typeResolver)
      context.put("swaggerGlobalSettings", settings)

    when:
      ApiModelReader apiModelReader = new ApiModelReader(modelProvider())
      apiModelReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      Map<String, Model> models = result.get("models")
      models.size() == 2
      models.containsKey("Entry«string,Pet»")
      models.containsKey("Pet")

  }

  def "Generates the correct models when alternateTypeProvider returns an ignoreable or base parameter type"() {
    given:
      HandlerMethod handlerMethod = handlerMethodIn(BusinessService, 'getResponseEntity', String)
      RequestMappingContext context =
            new RequestMappingContext(requestMappingInfo('/businesses/responseEntity/{businessId}'), handlerMethod)

      def settings = new SwaggerGlobalSettings()
      def config = new SpringSwaggerConfig()
      def modelConfig = new SwaggerModelsConfiguration()
      def typeResolver = new TypeResolver()
      settings.ignorableParameterTypes = config.defaultIgnorableParameterTypes()
      settings.alternateTypeProvider = modelConfig.alternateTypeProvider(typeResolver)
      settings.alternateTypeProvider.addRule(newRule(typeResolver.resolve(ResponseEntity.class, WildcardType.class),
            typeResolver.resolve(WildcardType.class)));
      settings.alternateTypeProvider.addRule(newRule(typeResolver.resolve(HttpEntity.class, WildcardType.class),
            typeResolver.resolve(WildcardType.class)));
      context.put("swaggerGlobalSettings", settings)

    when:
      ApiModelReader apiModelReader = new ApiModelReader(modelProvider())
      apiModelReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      Map<String, Model> models = result.get("models")
      models.size() == 0

  }

  def "property description should be populated when type is used in response and request body"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSameAnnotatedModelInReturnAndRequestBodyParam',
              DummyModels.AnnotatedBusinessModel
      )
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), handlerMethod)

      def settings = new SwaggerGlobalSettings()
      def config = new SpringSwaggerConfig()
      settings.ignorableParameterTypes = config.defaultIgnorableParameterTypes()
      def modelConfig = new SwaggerModelsConfiguration()
      def typeResolver = new TypeResolver()
      settings.alternateTypeProvider = modelConfig.alternateTypeProvider(typeResolver)
      context.put("swaggerGlobalSettings", settings)

    when:
      ApiModelReader apiModelReader = new ApiModelReader(modelProvider())
      apiModelReader.execute(context)
      Map<String, Model> result = context.getResult()

    then:
      Map<String, Model> models = result.get("models")
      models.size() == 1

      String modelName = DummyModels.AnnotatedBusinessModel.class.simpleName
      models.containsKey(modelName)

      Model model = models[modelName]
      Map modelProperties = fromScalaMap(model.properties())
      modelProperties.containsKey('name')

      ModelProperty nameProperty = modelProperties['name']
      nameProperty.description().isEmpty() == false

  }

  def "model should include property that is only visible during serialization"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSerializeOnlyPropInReturnAndRequestBodyParam',
              DummyModels.ModelWithSerializeOnlyProperty
      )
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), handlerMethod)

      def settings = new SwaggerGlobalSettings()
      def config = new SpringSwaggerConfig()
      settings.ignorableParameterTypes = config.defaultIgnorableParameterTypes()
      def modelConfig = new SwaggerModelsConfiguration()
      def typeResolver = new TypeResolver()
      settings.alternateTypeProvider = modelConfig.alternateTypeProvider(typeResolver)
      context.put("swaggerGlobalSettings", settings)

    when:
      ApiModelReader apiModelReader = new ApiModelReader(modelProvider())
      apiModelReader.execute(context)
      Map<String, Model> result = context.getResult()

    then:
      Map<String, Model> models = result.get("models")
      models.size() == 1

      String modelName = DummyModels.ModelWithSerializeOnlyProperty.class.simpleName
      models.containsKey(modelName)

      Model model = models[modelName]
      Map modelProperties = fromScalaMap(model.properties())
      modelProperties.size() == 2
      modelProperties.containsKey('visibleForSerialize')
      modelProperties.containsKey('alwaysVisible')

  }


  def "model should include snake_case property that is only visible during serialization when objectMapper has CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSerializeOnlyPropInReturnAndRequestBodyParam',
              DummyModels.ModelWithSerializeOnlyProperty
      )
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), handlerMethod)

      def settings = new SwaggerGlobalSettings()
      def config = new SpringSwaggerConfig()
      settings.ignorableParameterTypes = config.defaultIgnorableParameterTypes()
      def modelConfig = new SwaggerModelsConfiguration()
      def typeResolver = new TypeResolver()
      settings.alternateTypeProvider = modelConfig.alternateTypeProvider(typeResolver)
      context.put("swaggerGlobalSettings", settings)

    when:
      ApiModelReader apiModelReader = new ApiModelReader(modelProviderWithSnakeCaseNamingStrategy())
      apiModelReader.execute(context)
      Map<String, Model> result = context.getResult()

    then:
      Map<String, Model> models = result.get("models")
      models.size() == 1

      String modelName = DummyModels.ModelWithSerializeOnlyProperty.class.simpleName
      models.containsKey(modelName)

      Model model = models[modelName]
      Map modelProperties = fromScalaMap(model.properties())
      modelProperties.size() == 2
      modelProperties.containsKey('visible_for_serialize')
      modelProperties.containsKey('always_visible')

  }

  def "Test to verify issue #283"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestFoobarDto', FoobarDto)
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), handlerMethod)

      def settings = new SwaggerGlobalSettings()
      def config = new SpringSwaggerConfig()
      settings.ignorableParameterTypes = config.defaultIgnorableParameterTypes()
      def modelConfig = new SwaggerModelsConfiguration()
      def typeResolver = new TypeResolver()
      settings.alternateTypeProvider = modelConfig.alternateTypeProvider(typeResolver)
      context.put("swaggerGlobalSettings", settings)

    when:
      ApiModelReader apiModelReader = new ApiModelReader(modelProvider())
      apiModelReader.execute(context)
      Map<String, Model> result = context.getResult()

    then:
      Map<String, Model> models = result.get("models")
      models.size() == 1

      String modelName = FoobarDto.simpleName
      models.containsKey(modelName)

      Model model = models[modelName]
      Map modelProperties = fromScalaMap(model.properties())
      modelProperties.containsKey('visibleForSerialize')

  }
}
