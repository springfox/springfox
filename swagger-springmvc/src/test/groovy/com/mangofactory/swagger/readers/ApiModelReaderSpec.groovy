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
import com.mangofactory.swagger.models.dto.ApiDescription
import com.mangofactory.swagger.models.dto.Model
import com.mangofactory.swagger.models.dto.ModelProperty
import com.mangofactory.swagger.models.dto.Operation
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.method.HandlerMethod
import spock.lang.Specification

import javax.servlet.http.HttpServletResponse

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
      model.getName() == 'BusinessModel'
      model.getQualifiedType() == 'com.mangofactory.swagger.dummy.DummyModels$BusinessModel'

      Map<String, ModelProperty> modelProperties = model.getProperties()
      modelProperties.size() == 2

      ModelProperty nameProp = modelProperties['name']
      nameProp.getType().dataType.type == 'string'
      nameProp.getQualifiedType() == 'java.lang.String'
      nameProp.getPosition() == 0
      nameProp.isRequired() == false
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
      model.getName() == 'AnnotatedBusinessModel'
      model.getQualifiedType() == 'com.mangofactory.swagger.dummy.DummyModels$AnnotatedBusinessModel'

      Map<String, ModelProperty> modelProps = model.getProperties()
      ModelProperty prop = modelProps.name
      prop.getType().dataType.type == 'string'
      prop.getDescription() == 'The name of this business'
      prop.isRequired() == true

      modelProps.numEmployees.getDescription() == 'Total number of current employees'
      modelProps.numEmployees.isRequired() == false
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
      models['FunkyBusiness'].getQualifiedType() == 'com.mangofactory.swagger.dummy.DummyModels$FunkyBusiness'
  }

  def "Should pull models from operation's ApiResponse annotations"() {
    given:

      RequestMappingContext context = contextWithApiDescription(dummyHandlerMethod('methodAnnotatedWithApiResponse'), null)
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
      models.size() == 2
      models['RestError'].getQualifiedType() == 'com.mangofactory.swagger.dummy.RestError'
      models['Void'].getQualifiedType() == 'java.lang.Void'
  }

  def contextWithApiDescription(HandlerMethod handlerMethod, List<Operation> operationList = null) {
    RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), handlerMethod)
    def scalaOpList = null == operationList ? [] : operationList
    ApiDescription description = new ApiDescription(
            "anyPath",
            "anyDescription",
            scalaOpList,
            false
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
      Map modelProperties = model.getProperties()
      modelProperties.containsKey('name')

      ModelProperty nameProperty = modelProperties['name']
      nameProperty.getDescription().isEmpty() == false

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
      Map modelProperties = model.getProperties()
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
      Map modelProperties = model.getProperties()
      modelProperties.containsKey('visibleForSerialize')

  }
}
